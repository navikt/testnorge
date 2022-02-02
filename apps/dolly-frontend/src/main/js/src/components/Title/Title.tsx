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

const BetaTag = styled.h3`
	margin-bottom: 20px;
	padding-bottom: 8px;
`

export default ({ title, beta = false }: Props) => (
	<Title>
		<h1>{title}</h1>
		{beta && <BetaTag>(beta)</BetaTag>}
	</Title>
)
