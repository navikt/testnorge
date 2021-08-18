import React from 'react'
import styled from 'styled-components'
import NavButton from '~/components/ui/button/NavButton/NavButton'

const Container = styled.div`
	display: flex;
`
const Left = styled.div`
	padding: 20px 20px 20px 0;
	width: 30%;
	border-right: solid black 1px;
`
const Right = styled.div`
	padding: 20px 0 20px 20px;
	width: 70%;
`

const Button = styled(NavButton)`
	margin-top: 20px;
`

const Options = styled.div`
	overflow-y: scroll;
	height: 700px;
`

type Props = {
	left: React.ReactNode
	right: React.ReactNode
	buttonLabel?: string
	onSubmit: () => void
}

export default ({ left, right, buttonLabel = 'Søk', onSubmit }: Props) => (
	<Container>
		<Left>
			<Options>{left}</Options>
			<Button onClick={() => onSubmit()}>{buttonLabel}</Button>
		</Left>
		<Right>{right}</Right>
	</Container>
)
