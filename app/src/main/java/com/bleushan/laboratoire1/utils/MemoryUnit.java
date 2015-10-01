/*
 * MemoryUnit.java
 * Laboratoire1
 *
 * Copyright (c) 2015. Philippe Lafontaine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.bleushan.laboratoire1.utils;

/**
 * Memory unit size conversion utilities. Inspired by {@link java.util.concurrent.TimeUnit}
 */
public enum MemoryUnit {
  BIT {
    @Override
    public long toBit(long size) { return size; }

    @Override
    public long toByte(long size) { return size / (MemoryUnit.C1 / MemoryUnit.C0); }

    @Override
    public long toKilobyte(long size) { return size / (MemoryUnit.C2 / MemoryUnit.C0); }

    @Override
    public long toMegabyte(long size) { return size / (MemoryUnit.C3 / MemoryUnit.C0); }

    @Override
    public long toGigabyte(long size) { return size / (MemoryUnit.C4 / MemoryUnit.C0); }

    @Override
    public long toTerabyte(long size) { return size / (MemoryUnit.C5 / MemoryUnit.C0); }

    @Override
    public long toPetabyte(long size) { return size / (MemoryUnit.C6 / MemoryUnit.C0); }

    @Override
    public long convert(long size, MemoryUnit unit) { return unit.toBit(size); }
  },
  BYTE {
    @Override
    public long toBit(long size) {
      return MemoryUnit.scale(size,
                              MemoryUnit.C1 / MemoryUnit.C0,
                              (MemoryUnit.MAX / (MemoryUnit.C1 / MemoryUnit.C0)));
    }

    @Override
    public long toByte(long size) { return size; }

    @Override
    public long toKilobyte(long size) { return size / (MemoryUnit.C2 / MemoryUnit.C1); }

    @Override
    public long toMegabyte(long size) { return size / (MemoryUnit.C3 / MemoryUnit.C1); }

    @Override
    public long toGigabyte(long size) { return size / (MemoryUnit.C4 / MemoryUnit.C1); }

    @Override
    public long toTerabyte(long size) { return size / (MemoryUnit.C5 / MemoryUnit.C1); }

    @Override
    public long toPetabyte(long size) { return size / (MemoryUnit.C6 / MemoryUnit.C1); }

    @Override
    public long convert(long size, MemoryUnit unit) {return unit.toByte(size);}
  },
  KILOBYTE {
    @Override
    public long toBit(long size) {
      return MemoryUnit.scale(size,
                              MemoryUnit.C2 / MemoryUnit.C0,
                              (MemoryUnit.MAX /
                               (MemoryUnit.C2 / MemoryUnit.C0)));
    }

    @Override
    public long toByte(long size) {
      return MemoryUnit.scale(size,
                              MemoryUnit.C2 / MemoryUnit.C1,
                              (MemoryUnit.MAX /
                               (MemoryUnit.C2 / MemoryUnit.C1)));
    }

    @Override
    public long toKilobyte(long size) {return size;}

    @Override
    public long toMegabyte(long size) {return size / (MemoryUnit.C3 / MemoryUnit.C2);}

    @Override
    public long toGigabyte(long size) {return size / (MemoryUnit.C4 / MemoryUnit.C2);}

    @Override
    public long toTerabyte(long size) {return size / (MemoryUnit.C5 / MemoryUnit.C2);}

    @Override
    public long toPetabyte(long size) {return size / (MemoryUnit.C6 / MemoryUnit.C2);}

    @Override
    public long convert(long size, MemoryUnit unit) {return unit.toKilobyte(size);}
  },
  MEGABYTE {
    @Override
    public long toBit(long size) {
      return MemoryUnit.scale(size,
                              MemoryUnit.C3 / MemoryUnit.C0,
                              (MemoryUnit.MAX /
                               (MemoryUnit.C3 / MemoryUnit.C0)));
    }

    @Override
    public long toByte(long size) {
      return MemoryUnit.scale(size,
                              MemoryUnit.C3 / MemoryUnit.C1,
                              (MemoryUnit.MAX /
                               (MemoryUnit.C3 / MemoryUnit.C1)));
    }

    @Override
    public long toKilobyte(long size) {
      return MemoryUnit.scale(size,
                              MemoryUnit.C3 / MemoryUnit.C2,
                              (MemoryUnit.MAX /
                               (MemoryUnit.C3 / MemoryUnit.C2)));
    }

    @Override
    public long toMegabyte(long size) {return size;}

    @Override
    public long toGigabyte(long size) {return size / (MemoryUnit.C4 / MemoryUnit.C3);}

    @Override
    public long toTerabyte(long size) {return size / (MemoryUnit.C5 / MemoryUnit.C3);}

    @Override
    public long toPetabyte(long size) {return size / (MemoryUnit.C6 / MemoryUnit.C3);}

    @Override
    public long convert(long size, MemoryUnit unit) {return unit.toMegabyte(size);}
  },
  GIGABYTE {
    @Override
    public long toBit(long size) {
      return MemoryUnit.scale(size,
                              MemoryUnit.C4 / MemoryUnit.C0,
                              (MemoryUnit.MAX /
                               (MemoryUnit.C4 / MemoryUnit.C0)));
    }

    @Override
    public long toByte(long size) {
      return MemoryUnit.scale(size,
                              MemoryUnit.C4 / MemoryUnit.C1,
                              (MemoryUnit.MAX /
                               (MemoryUnit.C4 / MemoryUnit.C1)));
    }

    @Override
    public long toKilobyte(long size) {
      return MemoryUnit.scale(size,
                              MemoryUnit.C4 / MemoryUnit.C2,
                              (MemoryUnit.MAX /
                               (MemoryUnit.C4 / MemoryUnit.C2)));
    }

    @Override
    public long toMegabyte(long size) {
      return MemoryUnit.scale(size,
                              MemoryUnit.C4 / MemoryUnit.C3,
                              (MemoryUnit.MAX /
                               (MemoryUnit.C4 / MemoryUnit.C3)));
    }

    @Override
    public long toGigabyte(long size) { return size; }

    @Override
    public long toTerabyte(long size) {return size / (MemoryUnit.C5 / MemoryUnit.C4);}

    @Override
    public long toPetabyte(long size) {return size / (MemoryUnit.C6 / MemoryUnit.C4);}

    @Override
    public long convert(long size, MemoryUnit unit) {return unit.toGigabyte(size);}

  },
  TERABYTE {
    @Override
    public long toBit(long size) {
      return MemoryUnit.scale(size,
                              MemoryUnit.C5 / MemoryUnit.C0,
                              (MemoryUnit.MAX /
                               (MemoryUnit.C5 / MemoryUnit.C0)));
    }

    @Override
    public long toByte(long size) {
      return MemoryUnit.scale(size,
                              MemoryUnit.C5 / MemoryUnit.C1,
                              (MemoryUnit.MAX /
                               (MemoryUnit.C5 / MemoryUnit.C1)));
    }

    @Override
    public long toKilobyte(long size) {
      return MemoryUnit.scale(size,
                              MemoryUnit.C5 / MemoryUnit.C2,
                              (MemoryUnit.MAX /
                               (MemoryUnit.C5 / MemoryUnit.C2)));
    }

    @Override
    public long toMegabyte(long size) {
      return MemoryUnit.scale(size,
                              MemoryUnit.C5 / MemoryUnit.C3,
                              (MemoryUnit.MAX /
                               (MemoryUnit.C5 / MemoryUnit.C3)));
    }

    @Override
    public long toGigabyte(long size) {
      return MemoryUnit.scale(size,
                              MemoryUnit.C5 / MemoryUnit.C4,
                              (MemoryUnit.MAX /
                               (MemoryUnit.C5 / MemoryUnit.C4)));
    }

    @Override
    public long toTerabyte(long size) { return size; }

    @Override
    public long toPetabyte(long size) {return size / (MemoryUnit.C6 / MemoryUnit.C5);}

    @Override
    public long convert(long size, MemoryUnit unit) {return unit.toTerabyte(size);}

  },
  PETABYTE {
    @Override
    public long toBit(long size) {
      return MemoryUnit.scale(size,
                              MemoryUnit.C6 / MemoryUnit.C0,
                              (MemoryUnit.MAX /
                               (MemoryUnit.C6 / MemoryUnit.C0)));
    }

    @Override
    public long toByte(long size) {
      return MemoryUnit.scale(size,
                              MemoryUnit.C6 / MemoryUnit.C1,
                              (MemoryUnit.MAX /
                               (MemoryUnit.C6 / MemoryUnit.C1)));
    }

    @Override
    public long toKilobyte(long size) {
      return MemoryUnit.scale(size,
                              MemoryUnit.C6 / MemoryUnit.C2,
                              (MemoryUnit.MAX /
                               (MemoryUnit.C6 / MemoryUnit.C2)));
    }

    @Override
    public long toMegabyte(long size) {
      return MemoryUnit.scale(size,
                              MemoryUnit.C6 / MemoryUnit.C3,
                              (MemoryUnit.MAX /
                               (MemoryUnit.C6 / MemoryUnit.C3)));
    }

    @Override
    public long toGigabyte(long size) {
      return MemoryUnit.scale(size,
                              MemoryUnit.C6 / MemoryUnit.C4,
                              (MemoryUnit.MAX /
                               (MemoryUnit.C6 / MemoryUnit.C4)));
    }

    @Override
    public long toTerabyte(long size) {
      return MemoryUnit.scale(size,
                              MemoryUnit.C6 / MemoryUnit.C5,
                              (MemoryUnit.MAX /
                               (MemoryUnit.C6 / MemoryUnit.C5)));
    }

    @Override
    public long toPetabyte(long size) { return size; }

    @Override
    public long convert(long size, MemoryUnit unit) {return unit.toPetabyte(size);}

  };

  private static final long C0 = 1L;
  private static final long C1 = MemoryUnit.C0 * 8L;
  private static final long C2 = MemoryUnit.C1 * 1024L;
  private static final long C3 = MemoryUnit.C2 * 1024L;
  private static final long C4 = MemoryUnit.C3 * 1024L;
  private static final long C5 = MemoryUnit.C4 * 1024L;
  private static final long C6 = MemoryUnit.C5 * 1024L;
  private static final long MAX = Long.MAX_VALUE;

  /**
   * Scale s by m, checking for overflow.
   * This has a short name to make above code more readable.
   */
  private static long scale(long s, long m, long over) {
    if (s > over) {
      return Long.MAX_VALUE;
    }
    if (s < -over) {
      return Long.MIN_VALUE;
    }
    return s * m;
  }

  public long convert(long sourceSize, MemoryUnit sourceUnit) {
    throw new AbstractMethodError();
  }

  public long toBit(long size) {throw new AbstractMethodError();}

  public long toByte(long size) {throw new AbstractMethodError();}

  public long toKilobyte(long size) {throw new AbstractMethodError();}

  public long toMegabyte(long size) {throw new AbstractMethodError();}

  public long toGigabyte(long size) {throw new AbstractMethodError();}

  public long toTerabyte(long size) {throw new AbstractMethodError();}

  public long toPetabyte(long size) {throw new AbstractMethodError();}
}
