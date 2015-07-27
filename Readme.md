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

Note that you can define the color with `lineColor`

toggle in code:
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

Optionally these actions can also be done with Animation Duration

```
arrowView.toggle(1000);

arrowView.upArrow(800);

arrowView.downArrow(1600);
```

Forked from CrossView
https://github.com/cdflynn/crossview
