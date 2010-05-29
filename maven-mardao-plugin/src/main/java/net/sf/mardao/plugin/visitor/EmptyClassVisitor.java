package net.sf.mardao.plugin.visitor;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

public class EmptyClassVisitor implements ClassVisitor {

	@Override
	public void visit(int arg0, int arg1, String arg2, String arg3,
			String arg4, String[] arg5) {
		// TODO Auto-generated method stub

	}

	@Override
	public AnnotationVisitor visitAnnotation(String arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void visitAttribute(Attribute arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitEnd() {
		// TODO Auto-generated method stub

	}

	@Override
	public FieldVisitor visitField(int arg0, String arg1, String arg2,
			String arg3, Object arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void visitInnerClass(String arg0, String arg1, String arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public MethodVisitor visitMethod(int arg0, String arg1, String arg2,
			String arg3, String[] arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void visitOuterClass(String arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitSource(String arg0, String arg1) {
		// TODO Auto-generated method stub

	}

}
