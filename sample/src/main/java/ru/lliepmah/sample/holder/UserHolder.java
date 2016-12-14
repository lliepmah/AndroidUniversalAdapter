package ru.lliepmah.sample.holder;

import android.view.View;
import android.widget.TextView;

import ru.lliepmah.HolderBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.lliepmah.lib.DefaultViewHolder;
import ru.lliepmah.sample.R;
import ru.lliepmah.sample.model.Person;

/**
 * Created by Arthur Korchagin on 27.10.16
 */

@HolderBuilder(R.layout.li_user)
public class UserHolder extends DefaultViewHolder<Person> {

    @BindView(R.id.li_user_tv_name)
    TextView mTvName;
    @BindView(R.id.li_user_tv_lastname)
    TextView mTvLastName;

    private Person mPerson;
    private final OnPersonClickListener mOnPersonClickListener;

    public UserHolder(View itemView, OnPersonClickListener listener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mOnPersonClickListener = listener;
    }

    @Override
    public void bind(Person person) {
        mPerson = person;

        mTvName.setText(mPerson.getFirstName());
        mTvLastName.setText(mPerson.getLastName());
    }

    @OnClick(R.id.li_user_root)
    void onPersonClick() {
        if (mOnPersonClickListener != null) {
            mOnPersonClickListener.onPersonClick(mPerson);
        }
    }

   public interface OnPersonClickListener {
        void onPersonClick(Person person);
    }
}
