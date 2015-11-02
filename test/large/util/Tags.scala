package large.util

import org.scalatest.Tag

/**
 * We employ data-centric classification of tests on the basis of their:
 *   - speed
 *   - stability
 *
 * Small
 *   - fast AND stable
 *   - executes pre-deployment
 *   - blocks deployment on fail
 *
 * Large
 *   - slow OR flaky
 *   - executes post-deployment
 *   - does not block deployment on fail
 *
 * For details see test README.md
 *
 */

object Tags {
  object Large extends Tag("Large")
  // test is Small unless explicitly tagged as Large
}
