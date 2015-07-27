# ArrowView

![Sample](https://github.com/Abhiseshan/ArrowView/blob/master/Images/ArrowGif.gif?raw=true)

![Greeminder](https://github.com/Abhiseshan/ArrowView/blob/master/Images/Greeminder.gif?raw=true)

# Installation

```gradle
repositories {
    jcenter()
}

dependencies {
    compile 'me.abhiseshan.arrowview:arrowview:1.0.2'
}
```

# Implementation

Via XML
```
    <me.abhiseshan.arrowview.ArrowView
        android:id="@+id/sample_arrow_view"
        android:layout_width="50dp"
        android:layout_height="50dp"
        android:padding="10dp"
        app:av_lineColor="#ff2900"
        app:av_stroke="10"
        app:av_spin="inward"/>
```
Via CODE <BR><BR>
Toggle State
```
    final ArrowView arrowView = (ArrowView) findViewById(R.id.sample_arrow_view);
    arrowView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        arrowView.toggle();
      }
    });    
```

To manually set the state
```
//Brings the Arrow to point Upwards
arrowView.upArrow(); 

//Brings the Arrow to point Downwards
arrowView.downArrow();
```

Optionally states can also be set with Animation Duration

```
arrowView.toggle(1000);

arrowView.upArrow(800);

arrowView.downArrow(1600);
```

Programatically setting attributes

```
// Set the color of the arrowView
arrowView.setColor(int color); 

/*
    Sets the spin of the arrowView.
    Takes the arguements 
        SPIN_OUTWARD (Blue Arrow)
        SPIN_INWARD (Red Arrow)
*/
arrowView.setSpin();

// Set the stroke of the lines in the ArrowView
arrowView.setStroke(int stroke);
```

Forked from CrossView
https://github.com/cdflynn/crossview
