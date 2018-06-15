package com.glp.collie.util;

import java.util.ArrayList;
import java.util.List;

public class SplitUtils {

  /**
   * 分离列表参数中,增/删/改三个子集
   * 
   * @param target
   * @param source
   * @return
   */
  public static <T extends BizData<K>, K> SplitResult<T, K> split(List<T> target, List<T> source) {
    if (target == null && source == null)
      return null;

    if (target == null) {
      target = new ArrayList<>();
    }
    if (source == null) {
      source = new ArrayList<>();
    }

    SplitResult<T, K> result = new SplitResult<T, K>();

    // 全部删除
    if (target.isEmpty()) {
      for (T t : source)
        result.deleteIdList.add(t.getId());
      return result;
    }

    // 全部新增
    if (source.isEmpty()) {
      result.insertList.addAll(target);
      return result;
    }

    // 有新增+更新+删除
    for (T t : target) {
      // 更新的
      if (source.contains(t))
        result.updateList.add(t);
      else
        // 新增的
        result.insertList.add(t);
    }
    // 要删的
    for (T t : source)
      if (!target.contains(t))
        result.deleteIdList.add(t.getId());

    return result;
  }

  public static void main(String[] xxx) {
    Test t = new Test(null);
    Test t1 = new Test(1L);
    Test t2 = new Test(2L);
    Test t3 = new Test(3L);

    List<Test> list1 = new ArrayList<>();
    list1.add(t);
    list1.add(t1);
    list1.add(t2);

    List<Test> list2 = new ArrayList<>();
    list2.add(t2);
    list2.add(t3);

    SplitResult<Test, Long> result = split(list1, list2);

    System.out.println(result);
    System.out.println(t1.equals(t2));
    System.out.println(t2.equals(t3));
  }

  public static class Test implements BizData<Long> {
    private Long id;

    public Test(Long id) {
      this.id = id;
    }

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    @Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Test other = (Test) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			return true;
		}
    
  }

  /**
   * 拥有id属性的业务数据
   *
   * @param <K>
   *          id的类型
   */
  public interface BizData<K> {
    public boolean equals(Object o);

    public K getId();
  }

  /**
   * 拆分的增/删/改三个子集数据
   * 
   * @param <T>
   * @param <K>
   */
  public static class SplitResult<T, K> {
    private List<T> insertList;
    private List<K> deleteIdList;
    private List<T> updateList;

    public SplitResult() {
      insertList = new ArrayList<>();
      deleteIdList = new ArrayList<>();
      updateList = new ArrayList<>();
    }

    public List<T> getInsertList() {
      return insertList;
    }

    public void setInsertList(List<T> insertList) {
      this.insertList = insertList;
    }

    public List<K> getDeleteIdList() {
      return deleteIdList;
    }

    public void setDeleteIdList(List<K> deleteIdList) {
      this.deleteIdList = deleteIdList;
    }

    public List<T> getUpdateList() {
      return updateList;
    }

    public void setUpdateList(List<T> updateList) {
      this.updateList = updateList;
    }
  }
}
