package car.bkrc.com.car2021.ViewAdapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.util.List;

import car.bkrc.com.car2021.Utils.OtherUtil.RadiusUtil;
import car.bkrc.com.car2021.ActivityView.FirstActivity;
import car.bkrc.com.car2021.R;


public class ZigbeeAdapter extends RecyclerView.Adapter<ZigbeeAdapter.ViewHolder> {

    private List<Zigbee_Landmark> mZigbeeLandmarkList;
    static Context context = null;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View zigbeeView;
        ImageView zigbeeImage;
        TextView zigbeeName;


        public ViewHolder(View view) {
            super(view);
            zigbeeView = view;
            zigbeeImage = (ImageView) view.findViewById(R.id.landmark_image);
            zigbeeName = (TextView) view.findViewById(R.id.landmark_name);
        }
    }

    public ZigbeeAdapter(List<Zigbee_Landmark> zigbeeLandmarkList, Context context) {
        mZigbeeLandmarkList = zigbeeLandmarkList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.zigbee_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.zigbeeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Zigbee_Landmark zigbeeLandmark = mZigbeeLandmarkList.get(position);
                zigbee_select(zigbeeLandmark);
            }
        });
        holder.zigbeeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Zigbee_Landmark zigbeeLandmark = mZigbeeLandmarkList.get(position);
                zigbee_select(zigbeeLandmark);
            }
        });
        return holder;
    }

    private void zigbee_select(Zigbee_Landmark zigbeeLandmark) {
        switch (zigbeeLandmark.getName()) {
            case "???????????????":
                gateController();        // ???????????????
                break;
            case "LED???????????????":
                digital();                // LED???????????????
                break;
            case "?????????????????????":
                voiceController();    //?????????????????????
                break;
            case "?????????????????????":
                magnetic_suspension();  //?????????????????????
                break;
            case "??????TFT???????????????":
                TFT_Control();
                break;
            case "????????????????????????":
                Traffic_Control();
                break;
            case "?????????????????????":
                stereo_garage_Control();
                break;
            case "ETC???????????????":
                etc_Control();
                break;
            default:
                break;
        }

    }

    private void etc_Control() {
        AlertDialog.Builder garage_builder = new AlertDialog.Builder(context);
        garage_builder.setTitle("ETC?????????????????????????????????");
        String[] ga = {"??????????????????", "??????????????????"};
        garage_builder.setSingleChoiceItems(ga, -1, new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                switch (i) {
                    case 0: // ??????
                        etc_SteeringEngine_Adjust(0);
                        break;
                    case 1: // ??????
                        etc_SteeringEngine_Adjust(1);
                        break;
                    default:
                        break;
                }
            }

        });
        garage_builder.create().show();
    }

    private void stereo_garage_Control() {
        AlertDialog.Builder garage_builder = new AlertDialog.Builder(context);
        garage_builder.setTitle("?????????????????????");
        String[] ga = {"????????????????????????A???", "????????????????????????B???"};
        garage_builder.setSingleChoiceItems(ga, -1, new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                switch (i) {
                    case 0: // A
                        stereo_garage_A();
                        break;
                    case 1: // B
                        stereo_garage_B();
                        break;
                    default:
                        break;
                }
            }

        });
        garage_builder.create().show();
    }

    private void Traffic_Control() {
        AlertDialog.Builder traffic_builder = new AlertDialog.Builder(context);
        traffic_builder.setTitle("????????????????????????");
        String[] ga = {"???????????????????????????A???", "???????????????????????????B???"};
        traffic_builder.setSingleChoiceItems(ga, -1, new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                switch (i) {
                    case 0: // A
                        Traffic_light_A();
                        break;
                    case 1: // B
                        Traffic_light_B();
                        break;
                    default:
                        break;
                }
            }

        });
        traffic_builder.create().show();
    }

    private void TFT_Control() {
        AlertDialog.Builder tft_builder = new AlertDialog.Builder(context);
        tft_builder.setTitle("??????TFT???????????????");
        String[] ga = {"TFT??????????????????A???", "TFT??????????????????B???"};
        tft_builder.setSingleChoiceItems(ga, -1, new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                switch (i) {
                    case 0: // A
                        TFT_LCD_A();
                        break;
                    case 1: // B
                        TFT_LCD_B();
                        break;
                    default:
                        break;
                }
            }

        });
        tft_builder.create().show();
    }

    /**
     * ETC???????????????????????????????????????
     * @param rudder ???????????????0????????????1?????????
     */
    private void etc_SteeringEngine_Adjust(final int rudder) {
        AlertDialog.Builder garage_builder = new AlertDialog.Builder(context);
        String[] ga = {"??????", "??????"};
        if (rudder != 0){
            garage_builder.setTitle("????????????");

        }else  {
            garage_builder.setTitle("????????????");
        }
        garage_builder.setSingleChoiceItems(ga, -1, new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                switch (i) {
                    case 0:  // ??????
                        if (rudder != 0){
                            FirstActivity.Connect_Transport.rudder_control(0x00, 0x01);
                        }else FirstActivity.Connect_Transport.rudder_control(0x01, 0x00);
                        break;
                    case 1:  // ??????
                        if (rudder != 0){
                            FirstActivity.Connect_Transport.rudder_control(0x00,0x02);
                        }else FirstActivity.Connect_Transport.rudder_control(0x02,0x00);
                        break;
                    default:
                        break;
                }
            }

        });
        garage_builder.create().show();
    }

    private void stereo_garage_B() {
        AlertDialog.Builder garage_builder = new AlertDialog.Builder(context);
        garage_builder.setTitle("????????????????????????B???");
        String[] ga = {"?????????????????????", "???????????????", "???????????????", "???????????????", "????????????????????????????????????", "???????????????????????????/??????\n" +
                "????????????"};
        garage_builder.setSingleChoiceItems(ga, -1, new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                switch (i) {
                    case 0:  //???????????????
                        FirstActivity.Connect_Transport.garage_control(0x05,0x01, 0x01);
                        break;
                    case 1:  //???????????????
                        FirstActivity.Connect_Transport.garage_control(0x05,0x01, 0x02);
                        break;
                    case 2:  //???????????????
                        FirstActivity.Connect_Transport.garage_control(0x05,0x01, 0x03);
                        break;
                    case 3:  //???????????????
                        FirstActivity.Connect_Transport.garage_control(0x05,0x01, 0x04);
                        break;
                    case 4:  //?????????????????????????????????
                        FirstActivity.Connect_Transport.garage_control(0x05,0x02, 0x01);
                        break;
                    case 5:  //?????????????????????????????????
                        FirstActivity.Connect_Transport.garage_control(0x05,0x02, 0x02);
                        break;
                    default:
                        break;
                }
            }

        });
        garage_builder.create().show();
    }

    private void Traffic_light_B() {
        AlertDialog.Builder traffic_builder = new AlertDialog.Builder(context);
        traffic_builder.setTitle("???????????????????????????B???");
        String[] tr_light = {"??????????????????", "????????????????????????????????????", "????????????????????????????????????", "????????????????????????????????????"};
        traffic_builder.setSingleChoiceItems(tr_light, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                switch (i) {
                    case 0:
                        FirstActivity.Connect_Transport.traffic_control(0x0F,0x01, 0x00);
                        break;
                    case 1:
                        FirstActivity.Connect_Transport.traffic_control(0x0F,0x02, 0x01);
                        break;
                    case 2:
                        FirstActivity.Connect_Transport.traffic_control(0x0F,0x02, 0x02);
                        break;
                    case 3:
                        FirstActivity.Connect_Transport.traffic_control(0x0F,0x02, 0x03);
                        break;
                    default:
                        break;
                }

            }
        });
        traffic_builder.create().show();
    }

    private void TFT_LCD_B() {
        AlertDialog.Builder TFTbuilder = new AlertDialog.Builder(context);
        TFTbuilder.setTitle("TFT??????????????????B???");
        String[] TFTitem = {"??????????????????", "????????????", "????????????", "????????????", "HEX????????????"};
        TFTbuilder.setSingleChoiceItems(TFTitem, -1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        switch (which) {
                            case 0:
                                TFT_Image_B();
                                break;
                            case 1:
                                TFT_plate_number_B();
                                break;
                            case 2:
                                TFT_Timer_B();
                                break;
                            case 3:
                                Distance_B();
                                break;
                            case 4:
                                Hex_show_B();
                                break;
                            case 5:
                                TFT_traffic(0x08);
                                break;
                        }
                    }
                });
        TFTbuilder.create().show();
    }

    private void Hex_show_B() {
        AlertDialog.Builder TFT_Hex_builder = new AlertDialog.Builder(
                context);
        View view = LayoutInflater.from(context).inflate(
                R.layout.item_hex, null);
        TFT_Hex_builder.setTitle("HEX????????????");
        TFT_Hex_builder.setView(view);
        // ????????????
        final EditText editText1 = (EditText) view.findViewById(R.id.editText1);
        final EditText editText2 = (EditText) view.findViewById(R.id.editText2);
        final EditText editText3 = (EditText) view.findViewById(R.id.editText3);
        TFT_Hex_builder.setPositiveButton("??????",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        String ones = editText1.getText().toString();
                        String twos = editText2.getText().toString();
                        String threes = editText3.getText().toString();
                        // ????????????????????????????????????????????????????????????????????????????????????
                        one = ones.equals("") ? 0x00 : Integer.parseInt(ones,16);
                        two = twos.equals("") ? 0x00 : Integer.parseInt(twos,16);
                        three = threes.equals("") ? 0x00 : Integer.parseInt(threes,16);
                        FirstActivity.Connect_Transport.TFT_LCD(0x08, 0x40, one, two, three);
                    }
                });
        TFT_Hex_builder.setNegativeButton("??????",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.cancel();
                    }
                });
        TFT_Hex_builder.create().show();
    }

    private void Distance_B() {
        AlertDialog.Builder TFT_Distance_builder = new AlertDialog.Builder(context);
        TFT_Distance_builder.setTitle("??????????????????");
        String[] TFT_Image_item = {"400mm", "500mm", "600mm"};
        TFT_Distance_builder.setSingleChoiceItems(TFT_Image_item, -1, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO ???????????????????????????
                if (which == 0) {
                    FirstActivity.Connect_Transport.TFT_LCD(0x08,0x50, 0x00, 0x04, 0x00);
                }
                if (which == 1) {
                    FirstActivity.Connect_Transport.TFT_LCD(0x08,0x50, 0x00, 0x05, 0x00);
                }
                if (which == 2) {
                    FirstActivity.Connect_Transport.TFT_LCD(0x08,0x50, 0x00, 0x06, 0x00);
                }
            }
        });
        TFT_Distance_builder.create().show();
    }

    private void TFT_plate_number_B() {
        AlertDialog.Builder TFT_plate_builder = new AlertDialog.Builder(context);
        TFT_plate_builder.setTitle("??????????????????");
        final String[] TFT_Image_item = {"Z799C4", "B554H1", "D888B8"};
        TFT_plate_builder.setSingleChoiceItems(TFT_Image_item, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO ???????????????????????????
                switch (which) {
                    case 0:
                        FirstActivity.Connect_Transport.TFT_LCD(0x08,0x20, 'Z', '7', '9');
                        FirstActivity.Connect_Transport.yanchi(500);
                        FirstActivity.Connect_Transport.TFT_LCD(0x08,0x21, '9', 'C', '4');
                        break;
                    case 1:
                        FirstActivity.Connect_Transport.TFT_LCD(0x08,0x20, 'B', '5', '5');
                        FirstActivity.Connect_Transport.yanchi(500);
                        FirstActivity.Connect_Transport.TFT_LCD(0x08,0x21, '4', 'H', '1');
                        break;
                    case 2:
                        FirstActivity.Connect_Transport.TFT_LCD(0x08,0x20, 'D', '8', '8');
                        FirstActivity.Connect_Transport.yanchi(500);
                        FirstActivity.Connect_Transport.TFT_LCD(0x08,0x21, '8', 'B', '8');
                        break;
                }
            }
        });
        TFT_plate_builder.create().show();
    }

    private void TFT_Timer_B() {
        AlertDialog.Builder TFT_Iimer_builder = new AlertDialog.Builder(context);
        TFT_Iimer_builder.setTitle("????????????");
        String[] TFT_Image_item = {"??????", "??????", "??????"};
        TFT_Iimer_builder.setSingleChoiceItems(TFT_Image_item, -1, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO ???????????????????????????
                switch (which) {
                    case 0:
                        FirstActivity.Connect_Transport.TFT_LCD(0x08,0x30, 0x01, 0x00, 0x00);
                        break;
                    case 1:
                        FirstActivity.Connect_Transport.TFT_LCD(0x08,0x30, 0x02, 0x00, 0x00);
                        break;
                    case 2:
                        FirstActivity.Connect_Transport.TFT_LCD(0x08,0x30, 0x00, 0x00, 0x00);
                        break;
                }
            }
        });
        TFT_Iimer_builder.create().show();
    }

    private void TFT_Image_B() {
        AlertDialog.Builder TFT_Image_builder = new AlertDialog.Builder(context);
        TFT_Image_builder.setTitle("??????????????????");
        String[] TFT_Image_item = {"????????????", "????????????", "????????????", "????????????"};
        TFT_Image_builder.setSingleChoiceItems(TFT_Image_item, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO ???????????????????????????
                switch (which) {
                    case 0:
                        TFT_B_show();
                        break;
                    case 1:
                        FirstActivity.Connect_Transport.TFT_LCD(0x08,0x10, 0x01, 0x00, 0x00);
                        break;
                    case 2:
                        FirstActivity.Connect_Transport.TFT_LCD(0x08,0x10, 0x02, 0x00, 0x00);
                        break;
                    case 3:
                        FirstActivity.Connect_Transport.TFT_LCD(0x08,0x10, 0x03, 0x00, 0x00);
                        break;
                }
            }
        });
        TFT_Image_builder.create().show();
    }

    private Bitmap bitmap;
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Zigbee_Landmark zigbeeLandmark = mZigbeeLandmarkList.get(position);
        bitmap = BitmapFactory.decodeResource(context.getResources(),zigbeeLandmark.getImageId(),null);
        bitmap = RadiusUtil.roundBitmapByXfermode(bitmap, bitmap.getWidth(), bitmap.getHeight(), 10);
        holder.zigbeeImage.setImageBitmap(bitmap);
        holder.zigbeeName.setText(zigbeeLandmark.getName());
    }

    @Override
    public int getItemCount() {
        return mZigbeeLandmarkList.size();
    }


    //??????????????????????????????????????????
    private void Traffic_light_A() {
        AlertDialog.Builder traffic_builder = new AlertDialog.Builder(context);
        traffic_builder.setTitle("???????????????????????????A???");
        String[] tr_light = {"??????????????????", "????????????????????????????????????", "????????????????????????????????????", "????????????????????????????????????"};
        traffic_builder.setSingleChoiceItems(tr_light, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                switch (i) {
                    case 0:
                        FirstActivity.Connect_Transport.traffic_control(0x0E,0x01, 0x00);
                        break;
                    case 1:
                        FirstActivity.Connect_Transport.traffic_control(0x0E,0x02, 0x01);
                        break;
                    case 2:
                        FirstActivity.Connect_Transport.traffic_control(0x0E,0x02, 0x02);
                        break;
                    case 3:
                        FirstActivity.Connect_Transport.traffic_control(0x0E,0x02, 0x03);
                        break;
                    default:
                        break;
                }

            }
        });
        traffic_builder.create().show();
    }

    //????????????
    private void stereo_garage_A() {
        AlertDialog.Builder garage_builder = new AlertDialog.Builder(context);
        garage_builder.setTitle("????????????????????????A???");
        String[] ga = {"?????????????????????", "???????????????", "???????????????", "???????????????", "????????????????????????????????????", "???????????????????????????/??????\n" +
                "????????????"};
        garage_builder.setSingleChoiceItems(ga, -1, new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                switch (i) {
                    case 0:  //???????????????
                        FirstActivity.Connect_Transport.garage_control(0x0D,0x01, 0x01);
                        break;
                    case 1:  //???????????????
                        FirstActivity.Connect_Transport.garage_control(0x0D,0x01, 0x02);
                        break;
                    case 2:  //???????????????
                        FirstActivity.Connect_Transport.garage_control(0x0D,0x01, 0x03);
                        break;
                    case 3:  //???????????????
                        FirstActivity.Connect_Transport.garage_control(0x0D,0x01, 0x04);
                        break;
                    case 4:  //?????????????????????????????????
                        FirstActivity.Connect_Transport.garage_control(0x0D,0x02, 0x01);
                        break;
                    case 5:  //?????????????????????????????????
                        FirstActivity.Connect_Transport.garage_control(0x0D,0x02, 0x02);
                        break;
                    default:
                        break;
                }
            }

        });
        garage_builder.create().show();
    }

    private void gateController() {
        AlertDialog.Builder gt_builder = new AlertDialog.Builder(context);
        gt_builder.setTitle("???????????????");
        String[] gt = {"??????", "??????", "??????????????????", "????????????????????????","????????????????????????"};
        gt_builder.setSingleChoiceItems(gt, -1,
                (dialog, which) -> {
                    switch (which) {
                        case 0:
                            // ?????????????????????
                            FirstActivity.Connect_Transport.gate(0x01, 0x01, 0x00, 0x00);
                            break;
                        case 1:
                            // ?????????????????????
                            FirstActivity.Connect_Transport.gate(0x01, 0x02, 0x00, 0x00);
                            break;
                        case 2:
                            //????????????
                            gate_plate_number();
                            break;
                        case 3:
                            //??????????????????
                            gate_angle_number();
                            break;
                        case 4:
                            //?????????????????????????????????
                            FirstActivity.Connect_Transport.gate(0x20, 0x01, 0x00, 0x00);
                            break;
                        default:
                            break;
                    }
                });
        gt_builder.create().show();
    }


    private void gate_plate_number() {
        AlertDialog.Builder gate_plate_builder = new AlertDialog.Builder(context);
        gate_plate_builder.setTitle("??????????????????");
        final String[] gate_Image_item = {"A123B4", "B567C8", "D910E1"};
        gate_plate_builder.setSingleChoiceItems(gate_Image_item, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO ???????????????????????????
                switch (which) {
                    case 0:
                        FirstActivity.Connect_Transport.gate(0x10, 'A', '1', '2');
                        FirstActivity.Connect_Transport.yanchi(500);
                        FirstActivity.Connect_Transport.gate(0x11, '3', 'B', '4');
                        break;
                    case 1:
                        FirstActivity.Connect_Transport.gate(0x10, 'B', '5', '6');
                        FirstActivity.Connect_Transport.yanchi(500);
                        FirstActivity.Connect_Transport.gate(0x11, '7', 'C', '8');
                        break;
                    case 2:
                        FirstActivity.Connect_Transport.gate(0x10, 'D', '9', '1');
                        FirstActivity.Connect_Transport.yanchi(500);
                        FirstActivity.Connect_Transport.gate(0x11, '0', 'E', '1');
                        break;
                }
            }
        });
        gate_plate_builder.create().show();
    }


    private void gate_angle_number() {
        AlertDialog.Builder gate_plate_builder = new AlertDialog.Builder(context);
        gate_plate_builder.setTitle("????????????????????????");
        final String[] gate_Image_item = {"??????", "??????"};
        gate_plate_builder.setSingleChoiceItems(gate_Image_item, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO ???????????????????????????
                switch (which) {
                    case 0:
                        FirstActivity.Connect_Transport.gate(0x09, 0x01, 0, 0);
                        break;
                    case 1:
                        FirstActivity.Connect_Transport.gate(0x09, 0x02, 0, 0);
                        break;
                    default:
                        break;
                }
            }
        });
        gate_plate_builder.create().show();
    }

    private void digital() {// LED???????????????
        AlertDialog.Builder dig_timeBuilder = new AlertDialog.Builder(
                context);
        dig_timeBuilder.setTitle("LED???????????????");
        String[] dig_item = {"???????????????????????????", "???????????????????????????", "???????????????????????????"};
        dig_timeBuilder.setSingleChoiceItems(dig_item, -1,
                (dialog, which) -> {
                    // TODO Auto-generated method stub
                    if (which == 0) {// LED?????????????????????
                        digitalController();

                    } else if (which == 1) {// LED?????????????????????
                        digital_time();

                    } else if (which == 2) {// ????????????
                        digital_dis();

                    }
                });
        dig_timeBuilder.create().show();
    }

    // LED???????????????????????????
    private String[] itmes = {"?????????", "?????????"};
    int main, one, two, three;

    private void digitalController() {

        AlertDialog.Builder dg_Builder = new AlertDialog.Builder(
                context);
        View view = LayoutInflater.from(context).inflate(
                R.layout.item_digital, null);
        dg_Builder.setTitle("???????????????????????????");
        dg_Builder.setView(view);
        // ????????????
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        final EditText editText1 = (EditText) view.findViewById(R.id.editText1);
        final EditText editText2 = (EditText) view.findViewById(R.id.editText2);
        final EditText editText3 = (EditText) view.findViewById(R.id.editText3);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                context, android.R.layout.simple_spinner_item, itmes);
        spinner.setAdapter(adapter);
        // ????????????????????????
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                main = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
        dg_Builder.setPositiveButton("??????",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        String ones = editText1.getText().toString();
                        String twos = editText2.getText().toString();
                        String threes = editText3.getText().toString();
                        // ????????????????????????????????????????????????????????????????????????????????????
                        one = ones.equals("") ? 0x00 : Integer.parseInt(ones,16);
                        two = twos.equals("") ? 0x00 : Integer.parseInt(twos,16);
                        three = threes.equals("") ? 0x00 : Integer.parseInt(threes,16);
                        FirstActivity.Connect_Transport.digital(main, one, two, three);
                    }
                });

        dg_Builder.setNegativeButton("??????",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.cancel();
                    }
                });
        dg_Builder.create().show();
    }

    private int dgtime_index = -1;

    private void digital_time() {// LED?????????????????????
        AlertDialog.Builder dg_timeBuilder = new AlertDialog.Builder(
                context);
        dg_timeBuilder.setTitle("???????????????????????????");
        String[] dgtime_item = {"????????????", "????????????", "??????"};
        dg_timeBuilder.setSingleChoiceItems(dgtime_item, dgtime_index,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        if (which == 0) {// ????????????
                            FirstActivity.Connect_Transport.digital_close();

                        } else if (which == 1) {// ????????????
                            FirstActivity.Connect_Transport.digital_open();

                        } else if (which == 2) {// ????????????
                            FirstActivity.Connect_Transport.digital_clear();

                        }
                    }
                });
        dg_timeBuilder.create().show();
    }

    private void digital_dis() {
        AlertDialog.Builder dis_timeBuilder = new AlertDialog.Builder(
                context);
        dis_timeBuilder.setTitle("???????????????????????????");
        final String[] dis_item = {"100mm", "200mm", "400mm"};
        int dgdis_index = -1;
        dis_timeBuilder.setSingleChoiceItems(dis_item, dgdis_index,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {// ??????100mm
                            FirstActivity.Connect_Transport.digital_dic(Integer.parseInt(dis_item[which]
                                    .substring(0, 3)));
                        } else if (which == 1) {// ??????20mmm
                            FirstActivity.Connect_Transport.digital_dic(Integer.parseInt(dis_item[which]
                                    .substring(0, 3)));
                        } else if (which == 2) {// ??????400mm
                            FirstActivity.Connect_Transport.digital_dic(Integer.parseInt(dis_item[which]
                                    .substring(0, 3)));
                        }
                    }
                });
        dis_timeBuilder.create().show();
    }

    private TextView voiceText;

    private void voiceController() {
        AlertDialog.Builder dg_timeBuilder = new AlertDialog.Builder(
                context);
        dg_timeBuilder.setTitle("?????????????????????");
        String[] dgtime_item = {"????????????????????????", "????????????????????????"};
        dg_timeBuilder.setSingleChoiceItems(dgtime_item, dgtime_index,
                (dialog, which) -> {
                    // TODO Auto-generated method stub
                    if (which == 0) {// ????????????????????????
                        FirstActivity.Connect_Transport.VoiceBroadcast();

                    } else if (which == 1) {// ????????????????????????
                        View view = LayoutInflater.from(context).inflate(
                                R.layout.item_car, null);
                        voiceText = (EditText) view.findViewById(R.id.voiceText);

                        AlertDialog.Builder voiceBuilder = new AlertDialog.Builder(context);
                        voiceBuilder.setTitle("?????????????????????");
                        voiceBuilder.setView(view);
                        voiceBuilder.setPositiveButton("??????",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // TODO Auto-generated method stub
                                        String src = voiceText.getText().toString();
                                        if (src.equals("")) {
                                            src = "??????????????????????????????";
                                        }
                                        try {
                                            byte[] sbyte = bytesend(src.getBytes("GBK"));
                                            FirstActivity.Connect_Transport.send_voice(sbyte);
                                        } catch (UnsupportedEncodingException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                        dialog.cancel();
                                    }
                                });
                        voiceBuilder.setNegativeButton("??????", null);
                        voiceBuilder.create().show();
                    }
                });
        dg_timeBuilder.create().show();
    }

    private byte[] bytesend(byte[] sbyte) {
        byte[] textbyte = new byte[sbyte.length + 5];
        textbyte[0] = (byte) 0xFD;
        textbyte[1] = (byte) (((sbyte.length + 2) >> 8) & 0xff);
        textbyte[2] = (byte) ((sbyte.length + 2) & 0xff);
        textbyte[3] = 0x01;// ??????????????????
        textbyte[4] = (byte) 0x01;// ????????????
        for (int i = 0; i < sbyte.length; i++) {
            textbyte[i + 5] = sbyte[i];
        }
        return textbyte;
    }

    private void magnetic_suspension() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("?????????????????????");
        String[] item2 = {"???"};
        builder.setSingleChoiceItems(item2, -1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        if (which == 0) {
                            FirstActivity.Connect_Transport.magnetic_suspension(0x01, 0x01, 0x00, 0x00);
                        }
                    }
                });
        builder.create().show();
    }

    private void TFT_LCD_A() {
        AlertDialog.Builder TFTbuilder = new AlertDialog.Builder(context);
        TFTbuilder.setTitle("??????TFT??????????????????A???");
        String[] TFTitem = {"??????????????????", "??????????????????", "??????????????????", "??????????????????", "HEX????????????","????????????????????????"};
        TFTbuilder.setSingleChoiceItems(TFTitem, -1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        switch (which) {
                            case 0:
                                TFT_Image_A();
                                break;
                            case 1:
                                TFT_plate_number_A();
                                break;
                            case 2:
                                TFT_Timer_A();
                                break;
                            case 3:
                                Distance_A();
                                break;
                            case 4:
                                Hex_show_A();
                                break;
                            case 5:
                                TFT_traffic(0x0B);
                                break;
                        }
                    }
                });
        TFTbuilder.create().show();
    }

    private void TFT_Image_A() {
        AlertDialog.Builder TFT_Image_builder = new AlertDialog.Builder(context);
        TFT_Image_builder.setTitle("??????????????????");
        String[] TFT_Image_item = {"????????????", "????????????", "????????????", "????????????"};
        TFT_Image_builder.setSingleChoiceItems(TFT_Image_item, -1, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO ???????????????????????????
                switch (which) {
                    case 0:
                        TFT_A_show();
                        break;
                    case 1:
                        FirstActivity.Connect_Transport.TFT_LCD(0x0B,0x10, 0x01, 0x00, 0x00);
                        break;
                    case 2:
                        FirstActivity.Connect_Transport.TFT_LCD(0x0B,0x10, 0x02, 0x00, 0x00);
                        break;
                    case 3:
                        FirstActivity.Connect_Transport.TFT_LCD(0x0B,0x10, 0x03, 0x00, 0x00);
                        break;
                }
            }
        });
        TFT_Image_builder.create().show();
    }

    private void TFT_B_show() {
        AlertDialog.Builder TFT_Image_builder = new AlertDialog.Builder(context);
        TFT_Image_builder.setTitle("??????????????????");
        String[] TFT_Image_item = {"1", "2", "3", "4", "5"};
        TFT_Image_builder.setSingleChoiceItems(TFT_Image_item, -1, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO ???????????????????????????
                switch (which) {
                    case 0:
                        FirstActivity.Connect_Transport.TFT_LCD(0x08,0x10, 0x01, 0x00, 0x00);
                        break;
                    case 1:
                        FirstActivity.Connect_Transport.TFT_LCD(0x08,0x10, 0x02, 0x00, 0x00);
                        break;
                    case 2:
                        FirstActivity.Connect_Transport.TFT_LCD(0x08,0x10, 0x03, 0x00, 0x00);
                        break;
                    case 3:
                        FirstActivity.Connect_Transport.TFT_LCD(0x08,0x10, 0x04, 0x00, 0x00);
                        break;
                    case 4:
                        FirstActivity.Connect_Transport.TFT_LCD(0x08,0x10, 0x05, 0x00, 0x00);
                        break;
                }
            }
        });
        TFT_Image_builder.create().show();
    }

    private void TFT_A_show() {
        AlertDialog.Builder TFT_Image_builder = new AlertDialog.Builder(context);
        TFT_Image_builder.setTitle("??????????????????");
        String[] TFT_Image_item = {"1", "2", "3", "4", "5"};
        TFT_Image_builder.setSingleChoiceItems(TFT_Image_item, -1, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO ???????????????????????????
                switch (which) {
                    case 0:
                        FirstActivity.Connect_Transport.TFT_LCD(0x0B,0x10, 0x01, 0x00, 0x00);
                        break;
                    case 1:
                        FirstActivity.Connect_Transport.TFT_LCD(0x0B,0x10, 0x02, 0x00, 0x00);
                        break;
                    case 2:
                        FirstActivity.Connect_Transport.TFT_LCD(0x0B,0x10, 0x03, 0x00, 0x00);
                        break;
                    case 3:
                        FirstActivity.Connect_Transport.TFT_LCD(0x0B,0x10, 0x04, 0x00, 0x00);
                        break;
                    case 4:
                        FirstActivity.Connect_Transport.TFT_LCD(0x0B,0x10, 0x05, 0x00, 0x00);
                        break;
                }
            }
        });
        TFT_Image_builder.create().show();
    }


    private void TFT_plate_number_A() {
        AlertDialog.Builder TFT_plate_builder = new AlertDialog.Builder(context);
        TFT_plate_builder.setTitle("??????????????????");
        final String[] TFT_Image_item = {"A123B4", "B567C8", "D910E1"};
        TFT_plate_builder.setSingleChoiceItems(TFT_Image_item, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO ???????????????????????????
                switch (which) {
                    case 0:
                        FirstActivity.Connect_Transport.TFT_LCD(0x0B,0x20, 'A', '1', '2');
                        FirstActivity.Connect_Transport.yanchi(500);
                        FirstActivity.Connect_Transport.TFT_LCD(0x0B,0x21, '3', 'B', '4');
                        break;
                    case 1:
                        FirstActivity.Connect_Transport.TFT_LCD(0x0B,0x20, 'B', '5', '6');
                        FirstActivity.Connect_Transport.yanchi(500);
                        FirstActivity.Connect_Transport.TFT_LCD(0x0B,0x21, '7', 'C', '8');
                        break;
                    case 2:
                        FirstActivity.Connect_Transport.TFT_LCD(0x0B,0x20, 'D', '9', '1');
                        FirstActivity.Connect_Transport.yanchi(500);
                        FirstActivity.Connect_Transport.TFT_LCD(0x0B,0x21, '0', 'E', '1');
                        break;
                }
            }
        });
        TFT_plate_builder.create().show();
    }

    private void TFT_Timer_A() {
        AlertDialog.Builder TFT_Iimer_builder = new AlertDialog.Builder(context);
        TFT_Iimer_builder.setTitle("??????????????????");
        String[] TFT_Image_item = {"??????", "??????", "??????"};
        TFT_Iimer_builder.setSingleChoiceItems(TFT_Image_item, -1, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO ???????????????????????????
                switch (which) {
                    case 0:
                        FirstActivity.Connect_Transport.TFT_LCD(0x0B,0x30, 0x01, 0x00, 0x00);
                        break;
                    case 1:
                        FirstActivity.Connect_Transport.TFT_LCD(0x0B,0x30, 0x02, 0x00, 0x00);
                        break;
                    case 2:
                        FirstActivity.Connect_Transport.TFT_LCD(0x0B,0x30, 0x00, 0x00, 0x00);
                        break;
                }
            }
        });
        TFT_Iimer_builder.create().show();
    }

    private void Distance_A() {
        AlertDialog.Builder TFT_Distance_builder = new AlertDialog.Builder(context);
        TFT_Distance_builder.setTitle("??????????????????");
        String[] TFT_Image_item = {"100mm", "200mm", "300mm"};
        TFT_Distance_builder.setSingleChoiceItems(TFT_Image_item, -1, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO ???????????????????????????
                if (which == 0) {
                    FirstActivity.Connect_Transport.TFT_LCD(0x0B,0x50, 0x00, 0x01, 0x00);
                }
                if (which == 1) {
                    FirstActivity.Connect_Transport.TFT_LCD(0x0B,0x50, 0x00, 0x02, 0x00);
                }
                if (which == 2) {
                    FirstActivity.Connect_Transport.TFT_LCD(0x0B,0x50, 0x00, 0x03, 0x00);
                }
            }
        });
        TFT_Distance_builder.create().show();
    }

    private void Hex_show_A() {

        AlertDialog.Builder TFT_Hex_builder = new AlertDialog.Builder(
                context);
        View view = LayoutInflater.from(context).inflate(
                R.layout.item_hex, null);
        TFT_Hex_builder.setTitle("HEX????????????");
        TFT_Hex_builder.setView(view);
        // ????????????
        final EditText editText1 = (EditText) view.findViewById(R.id.editText1);
        final EditText editText2 = (EditText) view.findViewById(R.id.editText2);
        final EditText editText3 = (EditText) view.findViewById(R.id.editText3);
        TFT_Hex_builder.setPositiveButton("??????",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        String ones = editText1.getText().toString();
                        String twos = editText2.getText().toString();
                        String threes = editText3.getText().toString();
                        // ????????????????????????????????????????????????????????????????????????????????????
                        one = ones.equals("") ? 0x00 : Integer.parseInt(ones,16);
                        two = twos.equals("") ? 0x00 : Integer.parseInt(twos,16);
                        three = threes.equals("") ? 0x00 : Integer.parseInt(threes,16);
                        FirstActivity.Connect_Transport.TFT_LCD(0x0B, 0x40, one, two, three);
                    }
                });
        TFT_Hex_builder.setNegativeButton("??????",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.cancel();
                    }
                });
        TFT_Hex_builder.create().show();
    }

    private void TFT_traffic(final int type) {
        AlertDialog.Builder TFT_Iimer_builder = new AlertDialog.Builder(context);
        if (type != 0x0B){
            TFT_Iimer_builder.setTitle("TFT-B ????????????????????????");
        }else  TFT_Iimer_builder.setTitle("TFT-A ???????????????????????????");
        String[] TFT_Image_item = {"??????", "??????", "??????","??????","????????????","????????????"};
        TFT_Iimer_builder.setSingleChoiceItems(TFT_Image_item, -1, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO ???????????????????????????
                switch (which) {
                    case 0:
                        FirstActivity.Connect_Transport.TFT_LCD(type,0x60, 0x01, 0x00, 0x00);
                        break;
                    case 1:
                        FirstActivity.Connect_Transport.TFT_LCD(type,0x60, 0x02, 0x00, 0x00);
                        break;
                    case 2:
                        FirstActivity.Connect_Transport.TFT_LCD(type,0x60, 0x03, 0x00, 0x00);
                        break;
                    case 3:
                        FirstActivity.Connect_Transport.TFT_LCD(type,0x60, 0x04, 0x00, 0x00);
                        break;
                    case 4:
                        FirstActivity.Connect_Transport.TFT_LCD(type,0x60, 0x05, 0x00, 0x00);
                        break;
                    case 5:
                        FirstActivity.Connect_Transport.TFT_LCD(type,0x60, 0x06, 0x00, 0x00);
                        break;
                }
            }
        });
        TFT_Iimer_builder.create().show();
    }

}