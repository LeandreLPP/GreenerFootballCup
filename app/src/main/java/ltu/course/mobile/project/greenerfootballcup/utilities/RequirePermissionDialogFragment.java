package ltu.course.mobile.project.greenerfootballcup.utilities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import ltu.course.mobile.project.greenerfootballcup.R;

public class RequirePermissionDialogFragment extends DialogFragment {

    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1234;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.need_internet_message)
               .setPositiveButton(R.string.ok, (dialog, id) -> ActivityCompat.requestPermissions(
                       getActivity(),
                       new String[]{Manifest.permission.INTERNET},
                       MY_PERMISSIONS_REQUEST_READ_CONTACTS));
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
