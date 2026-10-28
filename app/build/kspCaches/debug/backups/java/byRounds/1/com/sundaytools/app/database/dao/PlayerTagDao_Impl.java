package com.sundaytools.app.database.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.sundaytools.app.database.entity.PlayerTagEntity;
import java.lang.Boolean;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class PlayerTagDao_Impl implements PlayerTagDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PlayerTagEntity> __insertionAdapterOfPlayerTagEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  private final SharedSQLiteStatement __preparedStmtOfUpdateStatus;

  private final SharedSQLiteStatement __preparedStmtOfUpdateRemoteId;

  public PlayerTagDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPlayerTagEntity = new EntityInsertionAdapter<PlayerTagEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `player_tags` (`id`,`name`,`tag`,`savedAtEpochMillis`,`dayKey`,`status`,`reason`,`remoteId`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PlayerTagEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getName());
        }
        statement.bindString(3, entity.getTag());
        statement.bindLong(4, entity.getSavedAtEpochMillis());
        statement.bindString(5, entity.getDayKey());
        statement.bindString(6, entity.getStatus());
        statement.bindString(7, entity.getReason());
        if (entity.getRemoteId() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getRemoteId());
        }
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM player_tags WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE player_tags SET status = ?, reason = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateRemoteId = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE player_tags SET remoteId = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final PlayerTagEntity tag, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfPlayerTagEntity.insertAndReturnId(tag);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateStatus(final long id, final String status, final String reason,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateStatus.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, status);
        _argIndex = 2;
        _stmt.bindString(_argIndex, reason);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateRemoteId(final long id, final String remoteId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateRemoteId.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, remoteId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateRemoteId.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<PlayerTagEntity>> observeTagsForDay(final String dayKey) {
    final String _sql = "SELECT * FROM player_tags WHERE dayKey = ? ORDER BY savedAtEpochMillis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, dayKey);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"player_tags"}, new Callable<List<PlayerTagEntity>>() {
      @Override
      @NonNull
      public List<PlayerTagEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfTag = CursorUtil.getColumnIndexOrThrow(_cursor, "tag");
          final int _cursorIndexOfSavedAtEpochMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "savedAtEpochMillis");
          final int _cursorIndexOfDayKey = CursorUtil.getColumnIndexOrThrow(_cursor, "dayKey");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfRemoteId = CursorUtil.getColumnIndexOrThrow(_cursor, "remoteId");
          final List<PlayerTagEntity> _result = new ArrayList<PlayerTagEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PlayerTagEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpTag;
            _tmpTag = _cursor.getString(_cursorIndexOfTag);
            final long _tmpSavedAtEpochMillis;
            _tmpSavedAtEpochMillis = _cursor.getLong(_cursorIndexOfSavedAtEpochMillis);
            final String _tmpDayKey;
            _tmpDayKey = _cursor.getString(_cursorIndexOfDayKey);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpRemoteId;
            if (_cursor.isNull(_cursorIndexOfRemoteId)) {
              _tmpRemoteId = null;
            } else {
              _tmpRemoteId = _cursor.getString(_cursorIndexOfRemoteId);
            }
            _item = new PlayerTagEntity(_tmpId,_tmpName,_tmpTag,_tmpSavedAtEpochMillis,_tmpDayKey,_tmpStatus,_tmpReason,_tmpRemoteId);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getTagsForDay(final String dayKey,
      final Continuation<? super List<PlayerTagEntity>> $completion) {
    final String _sql = "SELECT * FROM player_tags WHERE dayKey = ? ORDER BY savedAtEpochMillis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, dayKey);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PlayerTagEntity>>() {
      @Override
      @NonNull
      public List<PlayerTagEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfTag = CursorUtil.getColumnIndexOrThrow(_cursor, "tag");
          final int _cursorIndexOfSavedAtEpochMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "savedAtEpochMillis");
          final int _cursorIndexOfDayKey = CursorUtil.getColumnIndexOrThrow(_cursor, "dayKey");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfRemoteId = CursorUtil.getColumnIndexOrThrow(_cursor, "remoteId");
          final List<PlayerTagEntity> _result = new ArrayList<PlayerTagEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PlayerTagEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpTag;
            _tmpTag = _cursor.getString(_cursorIndexOfTag);
            final long _tmpSavedAtEpochMillis;
            _tmpSavedAtEpochMillis = _cursor.getLong(_cursorIndexOfSavedAtEpochMillis);
            final String _tmpDayKey;
            _tmpDayKey = _cursor.getString(_cursorIndexOfDayKey);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpRemoteId;
            if (_cursor.isNull(_cursorIndexOfRemoteId)) {
              _tmpRemoteId = null;
            } else {
              _tmpRemoteId = _cursor.getString(_cursorIndexOfRemoteId);
            }
            _item = new PlayerTagEntity(_tmpId,_tmpName,_tmpTag,_tmpSavedAtEpochMillis,_tmpDayKey,_tmpStatus,_tmpReason,_tmpRemoteId);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object existsForDay(final String dayKey, final String tag,
      final Continuation<? super Boolean> $completion) {
    final String _sql = "SELECT EXISTS(SELECT 1 FROM player_tags WHERE dayKey = ? AND tag = ?)";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, dayKey);
    _argIndex = 2;
    _statement.bindString(_argIndex, tag);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Boolean>() {
      @Override
      @NonNull
      public Boolean call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Boolean _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp != 0;
          } else {
            _result = false;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object existsAnywhere(final String tag, final Continuation<? super Boolean> $completion) {
    final String _sql = "SELECT EXISTS(SELECT 1 FROM player_tags WHERE tag = ?)";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, tag);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Boolean>() {
      @Override
      @NonNull
      public Boolean call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Boolean _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp != 0;
          } else {
            _result = false;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<String>> observeAllDayKeys() {
    final String _sql = "SELECT DISTINCT dayKey FROM player_tags ORDER BY dayKey DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"player_tags"}, new Callable<List<String>>() {
      @Override
      @NonNull
      public List<String> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<String> _result = new ArrayList<String>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final String _item;
            _item = _cursor.getString(0);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object countForDay(final String dayKey, final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM player_tags WHERE dayKey = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, dayKey);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllTags(final Continuation<? super List<PlayerTagEntity>> $completion) {
    final String _sql = "SELECT * FROM player_tags ORDER BY savedAtEpochMillis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PlayerTagEntity>>() {
      @Override
      @NonNull
      public List<PlayerTagEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfTag = CursorUtil.getColumnIndexOrThrow(_cursor, "tag");
          final int _cursorIndexOfSavedAtEpochMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "savedAtEpochMillis");
          final int _cursorIndexOfDayKey = CursorUtil.getColumnIndexOrThrow(_cursor, "dayKey");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfRemoteId = CursorUtil.getColumnIndexOrThrow(_cursor, "remoteId");
          final List<PlayerTagEntity> _result = new ArrayList<PlayerTagEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PlayerTagEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpTag;
            _tmpTag = _cursor.getString(_cursorIndexOfTag);
            final long _tmpSavedAtEpochMillis;
            _tmpSavedAtEpochMillis = _cursor.getLong(_cursorIndexOfSavedAtEpochMillis);
            final String _tmpDayKey;
            _tmpDayKey = _cursor.getString(_cursorIndexOfDayKey);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpRemoteId;
            if (_cursor.isNull(_cursorIndexOfRemoteId)) {
              _tmpRemoteId = null;
            } else {
              _tmpRemoteId = _cursor.getString(_cursorIndexOfRemoteId);
            }
            _item = new PlayerTagEntity(_tmpId,_tmpName,_tmpTag,_tmpSavedAtEpochMillis,_tmpDayKey,_tmpStatus,_tmpReason,_tmpRemoteId);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<PlayerTagEntity>> searchTagsForDay(final String dayKey, final String query) {
    final String _sql = "SELECT * FROM player_tags WHERE dayKey = ? AND tag LIKE '%' || ? || '%' ORDER BY savedAtEpochMillis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, dayKey);
    _argIndex = 2;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"player_tags"}, new Callable<List<PlayerTagEntity>>() {
      @Override
      @NonNull
      public List<PlayerTagEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfTag = CursorUtil.getColumnIndexOrThrow(_cursor, "tag");
          final int _cursorIndexOfSavedAtEpochMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "savedAtEpochMillis");
          final int _cursorIndexOfDayKey = CursorUtil.getColumnIndexOrThrow(_cursor, "dayKey");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfRemoteId = CursorUtil.getColumnIndexOrThrow(_cursor, "remoteId");
          final List<PlayerTagEntity> _result = new ArrayList<PlayerTagEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PlayerTagEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpTag;
            _tmpTag = _cursor.getString(_cursorIndexOfTag);
            final long _tmpSavedAtEpochMillis;
            _tmpSavedAtEpochMillis = _cursor.getLong(_cursorIndexOfSavedAtEpochMillis);
            final String _tmpDayKey;
            _tmpDayKey = _cursor.getString(_cursorIndexOfDayKey);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpRemoteId;
            if (_cursor.isNull(_cursorIndexOfRemoteId)) {
              _tmpRemoteId = null;
            } else {
              _tmpRemoteId = _cursor.getString(_cursorIndexOfRemoteId);
            }
            _item = new PlayerTagEntity(_tmpId,_tmpName,_tmpTag,_tmpSavedAtEpochMillis,_tmpDayKey,_tmpStatus,_tmpReason,_tmpRemoteId);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getById(final long id, final Continuation<? super PlayerTagEntity> $completion) {
    final String _sql = "SELECT * FROM player_tags WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<PlayerTagEntity>() {
      @Override
      @Nullable
      public PlayerTagEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfTag = CursorUtil.getColumnIndexOrThrow(_cursor, "tag");
          final int _cursorIndexOfSavedAtEpochMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "savedAtEpochMillis");
          final int _cursorIndexOfDayKey = CursorUtil.getColumnIndexOrThrow(_cursor, "dayKey");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfReason = CursorUtil.getColumnIndexOrThrow(_cursor, "reason");
          final int _cursorIndexOfRemoteId = CursorUtil.getColumnIndexOrThrow(_cursor, "remoteId");
          final PlayerTagEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpTag;
            _tmpTag = _cursor.getString(_cursorIndexOfTag);
            final long _tmpSavedAtEpochMillis;
            _tmpSavedAtEpochMillis = _cursor.getLong(_cursorIndexOfSavedAtEpochMillis);
            final String _tmpDayKey;
            _tmpDayKey = _cursor.getString(_cursorIndexOfDayKey);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpReason;
            _tmpReason = _cursor.getString(_cursorIndexOfReason);
            final String _tmpRemoteId;
            if (_cursor.isNull(_cursorIndexOfRemoteId)) {
              _tmpRemoteId = null;
            } else {
              _tmpRemoteId = _cursor.getString(_cursorIndexOfRemoteId);
            }
            _result = new PlayerTagEntity(_tmpId,_tmpName,_tmpTag,_tmpSavedAtEpochMillis,_tmpDayKey,_tmpStatus,_tmpReason,_tmpRemoteId);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
