package com.ikunkk02.flavorisenough.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class LiangziEntitySoundTest {
    private static final Path SOUND_DIR = Path.of(
            "src/main/resources/assets/flavor-is-enough-mod/sounds");

    @Test
    void liangziVoiceFilesAreMonoForPositionalFalloff() throws IOException {
        assertEquals(1, readVorbisChannelCount(SOUND_DIR.resolve("liangzi_sound_1.ogg")));
        assertEquals(1, readVorbisChannelCount(SOUND_DIR.resolve("liangzi_sound_2.ogg")));
    }

    @Test
    void liangziDoesNotUseTradeVoiceAsAmbientSound() {
        assertThrows(NoSuchMethodException.class,
                () -> LiangziEntity.class.getDeclaredMethod("getAmbientSound"));
    }

    private static int readVorbisChannelCount(Path path) throws IOException {
        byte[] bytes = Files.readAllBytes(path);
        byte[] marker = "vorbis".getBytes(java.nio.charset.StandardCharsets.US_ASCII);

        for (int i = 0; i <= bytes.length - marker.length; i++) {
            if (matches(bytes, marker, i)) {
                return Byte.toUnsignedInt(bytes[i + 10]);
            }
        }

        throw new IOException("Vorbis header not found: " + path);
    }

    private static boolean matches(byte[] bytes, byte[] marker, int offset) {
        for (int i = 0; i < marker.length; i++) {
            if (bytes[offset + i] != marker[i]) {
                return false;
            }
        }
        return true;
    }
}
