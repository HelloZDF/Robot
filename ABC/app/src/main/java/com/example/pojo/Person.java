package com.example.pojo;


import android.os.Parcel;
import android.os.Parcelable;

public class Person implements Parcelable {
    private  String name;
    private  int  age;


    protected Person(Parcel in) {
        name = in.readString();
        age = in.readInt();
    }

    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel in) {
            Person person  = new Person();
            person.name = in.readString();
            person.age = in.readInt();
            return  person;
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

    public Person() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(age);
    }
}
