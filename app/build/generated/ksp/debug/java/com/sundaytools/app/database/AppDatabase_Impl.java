package com.sundaytools.app.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.sundaytools.app.database.dao.PlayerTagDao;
import com.sundaytools.app.database.dao.PlayerTagDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile PlayerTagDao _playerTagDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(3) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `player_tags` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT, `tag` TEXT NOT NULL, `savedAtEpochMillis` INTEGER NOT NULL, `dayKey` TEXT NOT NULL, `status` TEXT NOT NULL, `reason` TEXT NOT NULL, `remoteId` TEXT)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_player_tags_tag_dayKey` ON `player_tags` (`tag`, `dayKey`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_player_tags_dayKey` ON `player_tags` (`dayKey`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '64793f44d4b365be137882d734b19b85')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `player_tags`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsPlayerTags = new HashMap<String, TableInfo.Column>(8);
        _columnsPlayerTags.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlayerTags.put("name", new TableInfo.Column("name", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlayerTags.put("tag", new TableInfo.Column("tag", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlayerTags.put("savedAtEpochMillis", new TableInfo.Column("savedAtEpochMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlayerTags.put("dayKey", new TableInfo.Column("dayKey", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlayerTags.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlayerTags.put("reason", new TableInfo.Column("reason", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlayerTags.put("remoteId", new TableInfo.Column("remoteId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPlayerTags = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPlayerTags = new HashSet<TableInfo.Index>(2);
        _indicesPlayerTags.add(new TableInfo.Index("index_player_tags_tag_dayKey", false, Arrays.asList("tag", "dayKey"), Arrays.asList("ASC", "ASC")));
        _indicesPlayerTags.add(new TableInfo.Index("index_player_tags_dayKey", false, Arrays.asList("dayKey"), Arrays.asList("ASC")));
        final TableInfo _infoPlayerTags = new TableInfo("player_tags", _columnsPlayerTags, _foreignKeysPlayerTags, _indicesPlayerTags);
        final TableInfo _existingPlayerTags = TableInfo.read(db, "player_tags");
        if (!_infoPlayerTags.equals(_existingPlayerTags)) {
          return new RoomOpenHelper.ValidationResult(false, "player_tags(com.sundaytools.app.database.entity.PlayerTagEntity).\n"
                  + " Expected:\n" + _infoPlayerTags + "\n"
                  + " Found:\n" + _existingPlayerTags);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "64793f44d4b365be137882d734b19b85", "ab1d7a87c3577f9a75e9a3e1581b54d6");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "player_tags");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `player_tags`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(PlayerTagDao.class, PlayerTagDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public PlayerTagDao playerTagDao() {
    if (_playerTagDao != null) {
      return _playerTagDao;
    } else {
      synchronized(this) {
        if(_playerTagDao == null) {
          _playerTagDao = new PlayerTagDao_Impl(this);
        }
        return _playerTagDao;
      }
    }
  }
}
