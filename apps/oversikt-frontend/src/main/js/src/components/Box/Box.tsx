import React from 'react'
import styled from 'styled-components'

type Props = {
	header: string
	children: React.ReactNode
}

const Box = styled.div`
	width: 350px;
	height: 300px;
	border: 1px solid;
	margin: 10px;
	padding: 12px;
	border-radius: 25px;
`

const Header = styled.h3`
	margin: 6px 0;
`

export default ({ children, header }: Props) => (
	<Box>
		<Header>{header}</Header>
		{children}
	</Box>
)
