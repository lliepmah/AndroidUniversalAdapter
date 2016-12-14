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
@HolderBuilder(R.layout.li_detail_user)
public class UserDetailHolder extends DefaultViewHolder<Person> {

    @BindView(R.id.li_user_detail_tv_name)
    TextView mTvName;
    @BindView(R.id.li_user_detail_tv_lastname)
    TextView mTvLastName;
    @BindView(R.id.li_user_detail_tv_phone)
    TextView mTvPhone;

    private Person mPerson;
    private final OnDetailPersonClickListener mOnPersonClickListener;

    public UserDetailHolder(View itemView, OnDetailPersonClickListener listener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mOnPersonClickListener = listener;
    }

    @Override
    public void bind(Person person) {
        mPerson = person;

        mTvName.setText(mPerson.getFirstName());
        mTvLastName.setText(mPerson.getLastName());
        mTvPhone.setText(mPerson.getPhone());
    }

    @OnClick(R.id.li_user_detail_root)
    void onPersonClick() {
        if (mOnPersonClickListener != null) {
            mOnPersonClickListener.onDetailPersonClick(mPerson);
        }
    }

    public interface OnDetailPersonClickListener {
        void onDetailPersonClick(Person person);
    }

}
