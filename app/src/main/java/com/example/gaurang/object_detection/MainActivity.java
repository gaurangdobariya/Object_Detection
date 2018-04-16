package com.example.gaurang.object_detection;

import android.Manifest;

import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.enums.EPickType;
import com.vansuita.pickimage.listeners.IPickCancel;
import com.vansuita.pickimage.listeners.IPickResult;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.ColorInfo;
import com.google.api.services.vision.v1.model.DominantColorsAnnotation;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.ImageProperties;
import com.google.api.services.vision.v1.model.SafeSearchAnnotation;

import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IPickResult {

    private static final int CAMERA_REQUEST_CODE = 1;
    private static final String TAG = "d";
    private Bitmap bitmap;

    Image image;
    Button takePicture;
    ProgressBar imageUploadProgress;

    private ImageView imageView;
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Packag";
    Feature feature;
    private TextView visionAPIData;
    MaterialBetterSpinner spinnerVisionAPI;
    public String[] visionAPI = new String[]{"LANDMARK_DETECTION", "LOGO_DETECTION", "SAFE_SEARCH_DETECTION", "IMAGE_PROPERTIES", "LABEL_DETECTION"};

    private String api = visionAPI[0];
    public static RecyclerView.Adapter adapter;
    public RecyclerView.LayoutManager layoutManager;
    public static RecyclerView recyclerView;
    public static ArrayList<DataModel> data;
    DataModel d1, d2, d3, d4, d5;
    PickImageDialog dialog;
    String message[];
    static View.OnClickListener myOnClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        takePicture = (Button) findViewById(R.id.takePicture);

        message = new String[5];

        myOnClickListener = new MyOnClickListener(this);
        recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        data = new ArrayList<DataModel>();
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PickSetup setup = new PickSetup()
                        .setTitle("Choose")
                        .setTitleColor(Color.WHITE)
                        .setBackgroundColor(Color.GRAY)
                        .setProgressTextColor(Color.WHITE)
                        .setCancelText("CANCEL")
                        .setCancelTextColor(Color.WHITE)
                        .setFlip(true)
                        .setMaxSize(500)
                        .setPickTypes(EPickType.GALLERY, EPickType.CAMERA)
                        .setCameraButtonText("Camera")
                        .setGalleryButtonText("Gallery")
                        .setIconGravity(Gravity.LEFT)
                        .setButtonOrientation(LinearLayoutCompat.VERTICAL)
                        .setSystemDialog(false);

                dialog = PickImageDialog.build(setup).show(MainActivity.this);
                dialog.setOnPickCancel(new IPickCancel() {
                    @Override
                    public void onCancelClick() {
                        dialog.dismiss();
                    }
                });
            }
        });


        feature = new Feature();
        feature.setType(visionAPI[0]);
        feature.setMaxResults(10);

        //  data.add(d3);



    }

    @Override
    public void onPickResult(PickResult r) {
        if (r.getError() == null) {
            bitmap = r.getBitmap();
            imageView.setImageBitmap(bitmap);
            image = getImageEncodeImage(bitmap);
            setData();
            //or     call here cloud visoin

        }
    }

    public void setData() {

        d1 = new DataModel(visionAPI[0], "Show More▼", "msg for landmark", R.drawable.gmd);
data.add(d1);
        d2 = new DataModel(visionAPI[1], "Show More▼", "msg for LOgo", R.drawable.gmd);
        data.add(d2);

        d3 = new DataModel(visionAPI[2], "Show More▼", "msg for safe search", R.drawable.gmd);
        data.add(d3);

        d4 = new DataModel(visionAPI[3], "Show More▼", "msg for image properties", R.drawable.gmd);
        data.add(d4);

        d5 = new DataModel(visionAPI[4], "Show More▼", "msg for LABEL_DETECTION", R.drawable.gmd);
        data.add(d5);


        adapter = new CustomAdapter(data);
            recyclerView.setAdapter(adapter);

    }


    @NonNull
    private Image getImageEncodeImage(Bitmap bitmap) {
        Image base64EncodedImage = new Image();
        // Convert the bitmap to a JPEG
        // Just in case it's a format that Android understands but Cloud Vision
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        // Base64 encode the JPEG
        base64EncodedImage.encodeContent(imageBytes);
        return base64EncodedImage;
    }

    private String convertResponseToString(BatchAnnotateImagesResponse response) {

        AnnotateImageResponse imageResponses = response.getResponses().get(0);

        List<EntityAnnotation> entityAnnotations;

        switch (api) {
            case "LANDMARK_DETECTION":
                message[0] = "default";
                entityAnnotations = imageResponses.getLandmarkAnnotations();
                message[0] = formatAnnotation(entityAnnotations);
                return message[0];

            case "LOGO_DETECTION":
                message[1] = "default";

                entityAnnotations = imageResponses.getLogoAnnotations();
                message[1] = formatAnnotation(entityAnnotations);
                return message[1];
            case "SAFE_SEARCH_DETECTION":
                message[2] = "default";

                SafeSearchAnnotation annotation = imageResponses.getSafeSearchAnnotation();
                message[2] = getImageAnnotation(annotation);
                return message[2];

            case "IMAGE_PROPERTIES":
                message[3] = "default";

                ImageProperties imageProperties = imageResponses.getImagePropertiesAnnotation();
                message[3] = getImageProperty(imageProperties);
                return message[3];
            case "LABEL_DETECTION":
                message[4] = "default";

                entityAnnotations = imageResponses.getLabelAnnotations();
                message[4] = formatAnnotation(entityAnnotations);
                return message[4];
        }
        return "default";
    }

    private String getImageAnnotation(SafeSearchAnnotation annotation) {
        return String.format("adult: %s\nmedical: %s\nspoofed: %s\nviolence: %s\n",
                annotation.getAdult(),
                annotation.getMedical(),
                annotation.getSpoof(),
                annotation.getViolence());
    }

    private String getImageProperty(ImageProperties imageProperties) {
        String message = "";
        DominantColorsAnnotation colors = imageProperties.getDominantColors();
        for (ColorInfo color : colors.getColors()) {
            message = message + "" + color.getPixelFraction() + " - " + color.getColor().getRed() + " - " + color.getColor().getGreen() + " - " + color.getColor().getBlue();
            message = message + "\n";
        }
        return message;
    }

    private String formatAnnotation(List<EntityAnnotation> entityAnnotation) {
        String message = "";

        if (entityAnnotation != null) {
            for (EntityAnnotation entity : entityAnnotation) {
                message = message + "    " + entity.getDescription() + " " + entity.getScore();
                message += "\n";
            }
        } else {
            message = "Nothing Found";
        }
        return message;
    }


    private void callCloudVision(final Bitmap bitmap, final Feature feature) {
//        visionAPIData.setText("Loading..");
        final List<Feature> featureList = new ArrayList<>();
        featureList.add(feature);

        final List<AnnotateImageRequest> annotateImageRequests = new ArrayList<>();

        AnnotateImageRequest annotateImageReq = new AnnotateImageRequest();
        annotateImageReq.setFeatures(featureList);
        annotateImageReq.setImage(getImageEncodeImage(bitmap));
        annotateImageRequests.add(annotateImageReq);


        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {

                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    VisionRequestInitializer requestInitializer = new VisionRequestInitializer("AIzaSyDx-atj9moCFMdJUe2x-93zHNXLlwAUtiE-MQo");

                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(requestInitializer);

                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest = new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(annotateImageRequests);

                    Vision.Images.Annotate annotateRequest = vision.images().annotate(batchAnnotateImagesRequest);
                    annotateRequest.setDisableGZipContent(true);
                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    return convertResponseToString(response);
                } catch (GoogleJsonResponseException e) {
                    Log.d(TAG, "failed to make API request because " + e.getContent());
                } catch (IOException e) {
                    Log.d(TAG, "failed to make API request because of other IOException " + e.getMessage());
                }
                return "Cloud Vision API request failed. Check logs for details." + feature.getType() + "";
            }

            protected void onPostExecute(String result) {
                //   visionAPIData.setText(result);
                // imageUploadProgress.setVisibility(View.INVISIBLE);
                // message[0]=result;
                appendData(result);
                Toast.makeText(getBaseContext(), "ok done" + result + ":", Toast.LENGTH_SHORT).show();

            }
        }.execute();
    }

    public void appendData(String result) {
        if (result != "default" || result != "") {
            switch (feature.getType()) {
                case "LANDMARK_DETECTION":
                    d1.setSm(result);
                    RecyclerView.ViewHolder viewHolder
                            = recyclerView.findViewHolderForPosition(0);


                    TextView textViewName
                            = (TextView) viewHolder.itemView.findViewById(R.id.card_view_show_more);
                    textViewName.setText(result);

                    break;

                case "LOGO_DETECTION":
                    d2 = new DataModel(visionAPI[1], "Show More▼", result, R.drawable.gmd);
                    RecyclerView.ViewHolder viewHolder1 = recyclerView.findViewHolderForPosition(1);
                    TextView textViewName1 = (TextView) viewHolder1.itemView.findViewById(R.id.card_view_show_more);
                    textViewName1.setText(result);
                    break;
                case "SAFE_SEARCH_DETECTION":
                    d3 = new DataModel(visionAPI[2], "Show More▼", result, R.drawable.gmd);
                    RecyclerView.ViewHolder viewHolder2 = recyclerView.findViewHolderForPosition(2);
                    TextView textViewName2 = (TextView) viewHolder2.itemView.findViewById(R.id.card_view_show_more);
                    textViewName2.setText(result);
                    break;
                case "IMAGE_PROPERTIES":
                    d4 = new DataModel(visionAPI[3], "Show More▼", result, R.drawable.gmd);
                    RecyclerView.ViewHolder viewHolder3 = recyclerView.findViewHolderForPosition(3);
                    TextView textViewName3 = (TextView) viewHolder3.itemView.findViewById(R.id.card_view_show_more);
                    textViewName3.setText(result);
                    break;

                case "LABEL_DETECTION":
                    d5 = new DataModel(visionAPI[4], "Show More▼", result, R.drawable.gmd);
                    RecyclerView.ViewHolder viewHolder4 = recyclerView.findViewHolderForPosition(4);
                    TextView textViewName4 = (TextView) viewHolder4.itemView.findViewById(R.id.card_view_show_more);
                    textViewName4.setText(result);
                    break;

            }


        }
    }


    public  class MyOnClickListener implements View.OnClickListener {

        private final Context context;

        public MyOnClickListener(Context context) {
            this.context = context;

        }

        @Override
        public void onClick(View v) {
            Coll_Expand(v);
        }

        private void Coll_Expand(View v) {
            int selectedItemPosition = recyclerView.getChildPosition(v);


            RecyclerView.ViewHolder viewHolder
                    = recyclerView.findViewHolderForPosition(selectedItemPosition);
            TextView temp
                    = (TextView) viewHolder.itemView.findViewById(R.id.card_view_location_description);


            TextView textViewName
                    = (TextView) viewHolder.itemView.findViewById(R.id.card_view_show_more);
            if (textViewName.isShown()) {
                temp.setText("Show More▼");
                textViewName.setVisibility(View.GONE);
            } else {
                temp.setText("Show Less▲");
                textViewName.setText("Loading...");
                Toast.makeText(getApplicationContext(),String.valueOf(selectedItemPosition),Toast.LENGTH_SHORT).show();
                feature.setType(visionAPI[selectedItemPosition]);
                textViewName.setText("Loading.....");

                feature.setMaxResults(10);
                textViewName.setText("Stil Loading........");

                if (bitmap != null)
                    callCloudVision(bitmap, feature);
                textViewName.setVisibility(View.VISIBLE);
            }
        }
    }
}