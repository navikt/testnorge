import React from 'react'
import styled from 'styled-components'

type Props = {
	title: string
	beta?: boolean
}

const Title = styled.div`
	display: flex;
	align-items: flex-end;
`

export default ({ title }: Props) => (
	<Title>
		<h1>{title}</h1>
	</Title>
)
