package com.relengxing.rebase.gray;

import com.relengxing.rebase.gray.GrayConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author relengxing
 * @date 2023-10-07 16:57
 * @Description
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({
        GrayConfiguration.class,
})
@Documented
@Inherited
public @interface EnableGray {
}
