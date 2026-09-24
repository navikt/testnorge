import React, { useState } from 'react'
import Loading from '@/components/ui/loading/Loading'
import { SearchField } from '@/components/searchField/SearchField'
import {
	useDollyOrganisasjonMalerBrukerOgMalnavn,
	useMalbestillingBruker,
} from '@/utils/hooks/useMaler'
import { Tabs } from '@navikt/ds-react'
import { MalPanel } from '@/pages/minSide/maler/MalPanel'
import { TestComponentSelectors } from '#/mocks/Selectors'
import { Buildings3Icon, MagnifyingGlassIcon, PersonGroupIcon } from '@navikt/aksel-icons'
import StyledAlert from '@/components/ui/alert/StyledAlert'
import { useSearchHotkey } from '@/utils/hooks/useSearchHotkey'
import { useSoekMalerBruker } from '@/utils/hooks/useTemplateSearch'

export enum MalType {
	PERSON = 'person',
	ORGANISASJON = 'organisasjon',
	TENORSOEK = 'tenorsoek',
}

export default ({ brukerId }: { brukerId: string }) => {
	const [searchText, setSearchText] = useState('')
	const [underRedigering, setUnderRedigering] = useState([])
	const searchInputRef = React.useRef(null)
	const shortcutKey = useSearchHotkey(searchInputRef)

	const { maler: egneMaler, loading, mutate } = useMalbestillingBruker(brukerId)

	const {
		maler: egneOrgMaler,
		loading: orgLoading,
		mutate: orgMutate,
	} = useDollyOrganisasjonMalerBrukerOgMalnavn(brukerId)

	const {
		maler: egneTenorsoekMaler,
		loading: tenorsoekLoading,
		mutate: tenorsoekMutate,
	} = useSoekMalerBruker(brukerId)

	if (loading || orgLoading || tenorsoekLoading) {
		return <Loading label="Loading" />
	}

	const antallEgneMaler = egneMaler?.length
	const antallEgneOrgMaler = egneOrgMaler?.length
	const antallEgneTenorsoekMaler = egneTenorsoekMaler?.length

	return (
		<div className="maloversikt">
			<hr />
			<div className="flexbox--align-center--space">
				<h2>Mine maler</h2>
				<SearchField
					placeholder={`Søk etter mal (${shortcutKey})`}
					setText={setSearchText}
					ref={searchInputRef}
					data-testid={TestComponentSelectors.INPUT_MINSIDE_SOEK_MAL}
				/>
			</div>
			{antallEgneMaler === 0 && antallEgneOrgMaler === 0 && antallEgneTenorsoekMaler === 0 ? (
				<StyledAlert variant={'info'}>
					Du har ingen maler enda. Neste gang du oppretter en ny person kan du lagre bestillingen
					som en mal på siste side av bestillingsveilederen.
				</StyledAlert>
			) : (
				<Tabs defaultValue={MalType.PERSON}>
					<Tabs.List>
						<Tabs.Tab
							data-testid={TestComponentSelectors.TOGGLE_MIN_SIDE_PERSONER_MALER}
							value={MalType.PERSON}
							label={'Personer'}
							icon={<PersonGroupIcon aria-hidden />}
						/>
						<Tabs.Tab
							data-testid={TestComponentSelectors.TOGGLE_MIN_SIDE_ORGANISASJON_MALER}
							value={MalType.ORGANISASJON}
							label={'Organisasjoner'}
							icon={<Buildings3Icon aria-hidden />}
						/>
						<Tabs.Tab
							value={MalType.TENORSOEK}
							label={'Tenor-søk'}
							icon={<MagnifyingGlassIcon aria-hidden />}
						/>
					</Tabs.List>
					<Tabs.Panel value={MalType.PERSON}>
						<MalPanel
							antallEgneMaler={antallEgneMaler}
							malListe={egneMaler}
							searchText={searchText}
							type={MalType.PERSON}
							mutate={() => {
								mutate()
								orgMutate()
								tenorsoekMutate()
							}}
							underRedigering={underRedigering}
							setUnderRedigering={setUnderRedigering}
						/>
					</Tabs.Panel>
					<Tabs.Panel value={MalType.ORGANISASJON}>
						<MalPanel
							antallEgneMaler={antallEgneOrgMaler}
							malListe={egneOrgMaler}
							searchText={searchText}
							type={MalType.ORGANISASJON}
							mutate={() => {
								mutate()
								orgMutate()
								tenorsoekMutate()
							}}
							underRedigering={underRedigering}
							setUnderRedigering={setUnderRedigering}
						/>
					</Tabs.Panel>
					<Tabs.Panel value={MalType.TENORSOEK}>
						<MalPanel
							antallEgneMaler={antallEgneTenorsoekMaler}
							malListe={egneTenorsoekMaler}
							searchText={searchText}
							type={MalType.TENORSOEK}
							mutate={() => {
								mutate()
								orgMutate()
								tenorsoekMutate()
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
