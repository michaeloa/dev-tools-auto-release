package no.elhub.common.build.configuration

import no.elhub.tools.autorelease.Version
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.nulls.shouldBeNull
import kotlin.collections.listOf

class VersionTest : DescribeSpec({

    describe("A version string") {

        it("0.1.0 should return major version 0, minor version 1, patch version 0 and prerelease null") {
            val sut = Version("0.1.0")
            sut.major shouldBe 0
            sut.minor shouldBe 1
            sut.patch shouldBe 0
            sut.preRelease.shouldBeNull()
        }

        it("1.2.3 should return major version 1, minor version 2, patch version 3 and prerelease null") {
            val sut = Version("1.2.3")
            sut.major shouldBe 1
            sut.minor shouldBe 2
            sut.patch shouldBe 3
            sut.preRelease.shouldBeNull()
        }

        it("1.2.3-RC.4 should return major version 1, minor version 2, patch version 3 and prerelease 4") {
            val sut = Version("1.2.3-RC.4")
            sut.major shouldBe 1
            sut.minor shouldBe 2
            sut.patch shouldBe 3
            sut.preRelease shouldBe 4
        }

        it("1.2 should throw an illegal state exception") {
            shouldThrow<IllegalStateException> {
                Version("1.2")
            }
        }

        it("1.B.3 should throw an illegal state exception") {
            shouldThrow<IllegalStateException> {
                Version("1.B.3")
            }
        }

    }

    describe("Two list of versions") {
        val v1 = Version("0.1.0")
        val v2 = Version("1.2.3")
        val v3 = Version("1.2.4-RC.3")
        val v4 = Version("1.2.4-RC.5")
        val v5 = Version("1.2.4")
        val v6 = Version("2.1.0")

        it ("should sort into the correct semantic order") {
            listOf(v4,v2,v1,v5,v3,v6).sorted() shouldBe listOf(v1,v2,v3,v4,v5,v6)
        }

    }

})
