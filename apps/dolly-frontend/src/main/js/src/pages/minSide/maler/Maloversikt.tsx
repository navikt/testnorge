import React, { useState } from 'react'
import Loading from '~/components/ui/loading/Loading'
import { SearchField } from '~/components/searchField/SearchField'
import {
	useDollyMalerBrukerOgMalnavn,
	useDollyOrganisasjonMalerBrukerOgMalnavn,
} from '~/utils/hooks/useMaler'
import { Alert } from '@navikt/ds-react'
import styled from 'styled-components'
import { MalPanel } from '~/pages/minSide/maler/MalPanel'

const StyledAlert = styled(Alert)`
	margin-bottom: 5px;
`

export default ({ brukerId }: { brukerId: string }) => {
	const [searchText, setSearchText] = useState('')
	const [underRedigering, setUnderRedigering] = useState([])

	const { maler: egneMaler, loading, mutate } = useDollyMalerBrukerOgMalnavn(brukerId)
	const {
		maler: egneOrgMaler,
		loading: orgLoading,
		mutate: orgMutate,
	} = useDollyOrganisasjonMalerBrukerOgMalnavn(brukerId)

	if (loading || orgLoading) {
		return <Loading label="Loading" />
	}

	const antallEgneMaler = egneMaler.length
	const antallEgneOrgMaler = egneOrgMaler.length

	return (
		<div className="maloversikt">
			<hr />
			<div className="flexbox--space">
				<h2>Mine maler</h2>
				<SearchField placeholder={'Søk etter mal'} setText={setSearchText} />
			</div>
			<MalPanel
				antallEgneMaler={antallEgneMaler}
				malListe={egneMaler}
				searchText={searchText}
				heading={'Personer'}
				iconType={'identifikasjon'}
				startOpen={true}
				mutate={() => {
					mutate()
					orgMutate()
				}}
				underRedigering={underRedigering}
				setUnderRedigering={setUnderRedigering}
			/>
			<MalPanel
				antallEgneMaler={antallEgneOrgMaler}
				malListe={egneOrgMaler}
				searchText={searchText}
				heading={'Organisasjoner'}
				iconType={'organisasjon'}
				startOpen={false}
				mutate={() => {
					mutate()
					orgMutate()
				}}
				underRedigering={underRedigering}
				setUnderRedigering={setUnderRedigering}
			/>
			{antallEgneMaler === 0 && antallEgneOrgMaler === 0 && (
				<StyledAlert variant={'info'}>
					Du har ingen maler enda. Neste gang du oppretter en ny person kan du lagre bestillingen
					som en mal på siste side av bestillingsveilederen.
				</StyledAlert>
			)}
		</div>
	)
}
