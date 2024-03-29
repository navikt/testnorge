import styled from 'styled-components'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import { IdenterPaths } from '@/pages/testnorgePage/search/partials/Identer'
import { IdentifikasjonPaths } from '@/pages/testnorgePage/search/partials/Identifikasjon'
import { PersonstatusPaths } from '@/pages/testnorgePage/search/partials/Personstatus'
import { AlderPaths } from '@/pages/testnorgePage/search/partials/Alder'
import { AdresserPaths } from '@/pages/testnorgePage/search/partials/Adresser'
import { NasjonalitetPaths } from '@/pages/testnorgePage/search/partials/Nasjonalitet'
import { RelasjonerPaths } from '@/pages/testnorgePage/search/partials/Relasjoner'
import { getCount } from '@/pages/testnorgePage/search/SearchOptions'
import React from 'react'
import { UseFormReturn } from 'react-hook-form/dist/types'

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
	margin: 20px 10px 0 0;
`

type Props = {
	left: React.ReactNode
	right: React.ReactNode
	formMethods: UseFormReturn
	onSubmit: () => void
	onEmpty: () => void
}

export default ({ left, right, formMethods, onSubmit, onEmpty }: Props) => {
	const getNumSelected = () => {
		const allPaths = [
			...IdenterPaths,
			...IdentifikasjonPaths,
			...PersonstatusPaths,
			...AlderPaths,
			...AdresserPaths,
			...NasjonalitetPaths,
			...RelasjonerPaths,
		]
		return getCount(allPaths, formMethods)
	}

	return (
		<Container>
			<Left>
				{left}
				<Button
					variant={'primary'}
					onClick={() => onSubmit()}
					disabled={!formMethods.formState.isValid}
				>
					{'Søk'}
				</Button>
				<Button variant={'secondary'} onClick={() => onEmpty()} disabled={getNumSelected() === 0}>
					{'Tøm'}
				</Button>
			</Left>
			<Right>{right}</Right>
		</Container>
	)
}
