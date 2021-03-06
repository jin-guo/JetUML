/*******************************************************************************
 * Fraco - Eclipse plug-in to detect fragile comments
 *
 * Copyright (C) 2020 McGill University
 *     
 * Eclipse Public License - v 2.0
 *
 *  THE ACCOMPANYING PROGRAM IS PROVIDED UNDER THE TERMS OF THIS ECLIPSE
 *  PUBLIC LICENSE v2.0. ANY USE, REPRODUCTION OR DISTRIBUTION
 *  OF THE PROGRAM CONSTITUTES RECIPIENT'S ACCEPTANCE OF THIS AGREEMENT.
 *******************************************************************************/
package ca.mcgill.cs.jetuml.viewers.nodes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Method;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.nodes.ClassNode;
import ca.mcgill.cs.jetuml.diagram.nodes.InterfaceNode;
import ca.mcgill.cs.jetuml.diagram.nodes.TypeNode;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;

public class TestTypeNodeViewer
{
	private static final TypeNodeViewer aViewer = new TypeNodeViewer();
	private final Method aMethodNameBoxHeight;
	
	public TestTypeNodeViewer() throws ReflectiveOperationException
	{
		aMethodNameBoxHeight = TypeNodeViewer.class.getDeclaredMethod("nameBoxHeight", 
				TypeNode.class, int.class, int.class);
		aMethodNameBoxHeight.setAccessible(true);
	}
	
	private int callNameBoxHeight(TypeNode pNode, int pAttributeBoxHeight, int pMethodBoxHeight)
	{
		try
		{
			return (int) aMethodNameBoxHeight.invoke(aViewer, pNode, pAttributeBoxHeight, pMethodBoxHeight);
		}
		catch( ReflectiveOperationException e )
		{
			fail("Reflection problem: " + e.getMessage());
			return -1;
		}
	}
	
	@BeforeAll
	public static void setupClass()
	{
		JavaFXLoader.load();
	}
	
	@ParameterizedTest
	@MethodSource("provideArgumentsForTestBounds")
	public void testBounds(TypeNode pNode, Rectangle pOracle)
	{
		assertEquals(pOracle, aViewer.getBounds(pNode));
	}
	
	private static Stream<Arguments> provideArgumentsForTestBounds() {
	    return Stream.of(
	      createInterfaceNode1(),
	      createInterfaceNode2(),
	      createInterfaceNode3(),
	      createInterfaceNode4(),
	      createInterfaceNode5(),
	      createInterfaceNode6()
	    );
	}
	
	// At (0,0); name is just the interface prototype, no methods
	private static Arguments createInterfaceNode1()
	{
		return Arguments.of(new InterfaceNode(), 
				new Rectangle(0,0, 100, 60)); // Default width and height
	}
	
	// At (10,20); name is just the interface prototype, no methods
	private static Arguments createInterfaceNode2()
	{
		InterfaceNode node = new InterfaceNode();
		node.moveTo(new Point(10,20));
		return Arguments.of(node, 
				new Rectangle(10, 20, 100, 60)); // Default width and height, translated
	}
	
	// At (0,0), name is a single line
	private static Arguments createInterfaceNode3()
	{
		InterfaceNode node = new InterfaceNode();
		node.setName(node.getName() + "NAME");
		return Arguments.of(node, 
				new Rectangle(0, 0, 100, 60)); // Default width and height
	}
	
	// At (0,0) name is two lines, no methods
	private static Arguments createInterfaceNode4()
	{
		InterfaceNode node = new InterfaceNode();
		node.setName(node.getName() + "NAME");
		return Arguments.of(node, 
				new Rectangle(0, 0, 100, 60)); // Default width and height
	}
	
	// At (0,0) name is three lines, no methods
	private static Arguments createInterfaceNode5()
	{
		InterfaceNode node = new InterfaceNode();
		node.setName(node.getName() + "NAME1\nNAME2\nNAME3");
		return Arguments.of(node, 
				new Rectangle(0, 0, 100, 71)); // Default width and additional height
	}

	// Name is just the interface prototype, one methods
	private static Arguments createInterfaceNode6()
	{
		InterfaceNode node = new InterfaceNode();
		node.setMethods("METHODS");
		return Arguments.of(node, 
				new Rectangle(0, 0, 100, 60)); // Default width and height
	}
	
	@Test
	public void testNameBoxHeight_OneLineName()
	{
		assertEquals(60, callNameBoxHeight(new InterfaceNode(), 0, 0));
	}
	
	@Test
	public void testNameBoxHeight_MultiLineName()
	{
		InterfaceNode node = new InterfaceNode();
		node.setName("X\nX\nX\nX\nX");
		assertTrue(callNameBoxHeight(node, 0, 0) > 60);
	}
	
	@Test
	public void testNameBoxHeight_OneLineNameAndAttribute()
	{
		ClassNode node = new ClassNode();
		assertEquals(40, callNameBoxHeight(node, 20, 0));
	}
	
	@Test
	public void testNameBoxHeight_OneLineNameAndAttributeAndMethods()
	{
		ClassNode node = new ClassNode();
		assertEquals(20, callNameBoxHeight(node, 20, 40));
	}
}
