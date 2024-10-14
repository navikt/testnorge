import React, { useState } from 'react'
import Loading from '@/components/ui/loading/Loading'
import { SearchField } from '@/components/searchField/SearchField'
import {
	useDollyMalerBrukerOgMalnavn,
	useDollyOrganisasjonMalerBrukerOgMalnavn,
} from '@/utils/hooks/useMaler'
import { Tabs } from '@navikt/ds-react'
import { MalPanel } from '@/pages/minSide/maler/MalPanel'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { Buildings3Icon, PersonGroupIcon } from '@navikt/aksel-icons'
import StyledAlert from '@/components/ui/alert/StyledAlert'

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

	const antallEgneMaler = egneMaler?.length
	const antallEgneOrgMaler = egneOrgMaler?.length

	return (
		<div className="maloversikt">
			<hr />
			<div className="flexbox--space">
				<h2>Mine maler</h2>
				<SearchField
					data-testid={TestComponentSelectors.INPUT_MINSIDE_SOEK_MAL}
					placeholder={'Søk etter mal'}
					setText={setSearchText}
				/>
			</div>
			{antallEgneMaler === 0 && antallEgneOrgMaler === 0 ? (
				<StyledAlert variant={'info'}>
					Du har ingen maler enda. Neste gang du oppretter en ny person kan du lagre bestillingen
					som en mal på siste side av bestillingsveilederen.
				</StyledAlert>
			) : (
				<Tabs defaultValue={'personer'}>
					<Tabs.List>
						<Tabs.Tab
							data-testid={TestComponentSelectors.TOGGLE_MIN_SIDE_PERSONER_MALER}
							value={'personer'}
							label={'Personer'}
							icon={<PersonGroupIcon />}
						/>
						<Tabs.Tab
							data-testid={TestComponentSelectors.TOGGLE_MIN_SIDE_ORGANISASJON_MALER}
							value={'organisasjoner'}
							label={'Organisasjoner'}
							icon={<Buildings3Icon />}
						/>
					</Tabs.List>
					<Tabs.Panel value={'personer'}>
						<MalPanel
							antallEgneMaler={antallEgneMaler}
							malListe={egneMaler}
							searchText={searchText}
							type={'person'}
							mutate={() => {
								mutate()
								orgMutate()
							}}
							underRedigering={underRedigering}
							setUnderRedigering={setUnderRedigering}
						/>
					</Tabs.Panel>
					<Tabs.Panel value={'organisasjoner'}>
						<MalPanel
							antallEgneMaler={antallEgneOrgMaler}
							malListe={egneOrgMaler}
							searchText={searchText}
							type={'organisasjon'}
							mutate={() => {
								mutate()
								orgMutate()
							}}
							underRedigering={underRedigering}
							setUnderRedigering={setUnderRedigering}
						/>
					</Tabs.Panel>
				</Tabs>
			)}
		</div>
	)
}
