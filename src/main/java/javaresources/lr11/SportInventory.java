package javaresources.lr11;

import javafx.beans.property.*;

public class SportInventory {
     private SimpleIntegerProperty ID;
     private SimpleStringProperty title;
     private SimpleStringProperty descrpiton;
     private SimpleIntegerProperty count;
     private SimpleDoubleProperty costPerHour;
     private SimpleStringProperty photoPath;
     private SimpleBooleanProperty enabled;

     public SportInventory(int ID, String title, String description, int count, double costPerHour,String photoPath, boolean enabled){
          this.ID=new SimpleIntegerProperty(ID);
          this.title=new SimpleStringProperty(title);
          this.descrpiton=new SimpleStringProperty(description);
          this.count=new SimpleIntegerProperty(count);
          this.costPerHour=new SimpleDoubleProperty(costPerHour);
          this.photoPath=new SimpleStringProperty(photoPath);
          this.enabled=new SimpleBooleanProperty(enabled);
     }
     public int getID() {
          return ID.get();
     }
     public String getTitle() {
          return title.get();
     }
     public String getDescription() {
          return descrpiton.get();
     }
     public int getCount() {
          return count.get();
     }
     public double getCostPerHour() {
          return costPerHour.get();
     }
     public String getPhotoPath(){
          return photoPath.get();
     }
     public boolean getEnabled(){
          return enabled.get();
     }
     public void setEnabled(boolean enabled){
          this.enabled=new SimpleBooleanProperty(enabled);
     }
     public String toString(){
          return this.getTitle();
     }
}
