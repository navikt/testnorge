import React from 'react'
import { AdresseKodeverk } from '~/config/kodeverk'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import {
	Statsborgerskap,
	UtflyttingFraNorge,
	InnflyttingTilNorge,
	HentPerson,
} from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { PdlStatsborgerskap } from '~/components/fagsystem/pdl/visning/partials/nasjonalitet/PdlStatsborgerskap'
import { PdlInnflytting } from '~/components/fagsystem/pdl/visning/partials/nasjonalitet/PdlInnflytting'
import { PdlUtflytting } from '~/components/fagsystem/pdl/visning/partials/nasjonalitet/PdlUtflytting'

type NasjonalitetProps = {
	data: HentPerson
	visTittel?: boolean
}

export const PdlNasjonalitet = ({ data, visTittel = true }: NasjonalitetProps) => {
	const { statsborgerskap, innflyttingTilNorge, utflyttingFraNorge } = data
	if (
		statsborgerskap?.length < 1 &&
		innflyttingTilNorge?.length < 1 &&
		utflyttingFraNorge?.length < 1
	)
		return null

	return (
		<div>
			{visTittel && <SubOverskrift label="Nasjonalitet" iconKind="nasjonalitet" />}
			<PdlStatsborgerskap statsborgerskapListe={statsborgerskap} />
			<PdlInnflytting innflytting={innflyttingTilNorge} />
			<PdlUtflytting utflytting={utflyttingFraNorge} />
			{/*{innflyttingTilNorge?.length > 0 && (*/}
			{/*	<ErrorBoundary>*/}
			{/*		<DollyFieldArray data={innflyttingTilNorge} header={'Innvandret'} nested>*/}
			{/*			{(innvandring: InnflyttingTilNorge) => (*/}
			{/*				<>*/}
			{/*					<TitleValue*/}
			{/*						title="Fraflyttingsland"*/}
			{/*						value={innvandring.fraflyttingsland}*/}
			{/*						kodeverk={AdresseKodeverk.InnvandretUtvandretLand}*/}
			{/*					/>*/}
			{/*					<TitleValue*/}
			{/*						title="Fraflyttingssted"*/}
			{/*						value={innvandring.fraflyttingsstedIUtlandet}*/}
			{/*					/>*/}
			{/*				</>*/}
			{/*			)}*/}
			{/*		</DollyFieldArray>*/}
			{/*	</ErrorBoundary>*/}
			{/*)}*/}

			{/*{utflyttingFraNorge?.length > 0 && (*/}
			{/*	<ErrorBoundary>*/}
			{/*		<DollyFieldArray data={utflyttingFraNorge} header={'Utvandret'} nested>*/}
			{/*			{(utvandring: UtflyttingFraNorge) => (*/}
			{/*				<>*/}
			{/*					<TitleValue*/}
			{/*						title="Tilflyttingsland"*/}
			{/*						value={utvandring.tilflyttingsland}*/}
			{/*						kodeverk={AdresseKodeverk.InnvandretUtvandretLand}*/}
			{/*					/>*/}
			{/*					<TitleValue title="Tilflyttingssted" value={utvandring.tilflyttingsstedIUtlandet} />*/}
			{/*				</>*/}
			{/*			)}*/}
			{/*		</DollyFieldArray>*/}
			{/*	</ErrorBoundary>*/}
			{/*)}*/}
		</div>
	)
}
