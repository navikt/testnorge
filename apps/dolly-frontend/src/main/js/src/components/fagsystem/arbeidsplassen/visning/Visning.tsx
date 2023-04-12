import Loading from '@/components/ui/loading/Loading'
import React from 'react'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { UtdanningVisning } from '@/components/fagsystem/arbeidsplassen/visning/partials/UtdanningVisning'
import { FagbrevVisning } from '@/components/fagsystem/arbeidsplassen/visning/partials/FagbrevVisning'
import { ArbeidserfaringVisning } from '@/components/fagsystem/arbeidsplassen/visning/partials/ArbeidserfaringVisning'
import { AnnenErfaringVisning } from '@/components/fagsystem/arbeidsplassen/visning/partials/AnnenErfaringVisning'
import { KompetanserVisning } from '@/components/fagsystem/arbeidsplassen/visning/partials/KompetanserVisning'
import { OffentligeGodkjenningerVisning } from '@/components/fagsystem/arbeidsplassen/visning/partials/OffentligeGodkjenningerVisning'
import { AndreGodkjenningerVisning } from '@/components/fagsystem/arbeidsplassen/visning/partials/AndreGodkjenningerVisning'
import { SpraakVisning } from '@/components/fagsystem/arbeidsplassen/visning/partials/SpraakVisning'
import { FoererkortVisning } from '@/components/fagsystem/arbeidsplassen/visning/partials/FoererkortVisning'
import { KursVisning } from '@/components/fagsystem/arbeidsplassen/visning/partials/KursVisning'
import { SammendragVisning } from '@/components/fagsystem/arbeidsplassen/visning/partials/SammendragVisning'
import { JobboenskerVisning } from '@/components/fagsystem/arbeidsplassen/visning/partials/JobboenskerVisning'
import Panel from '@/components/ui/panel/Panel'
import { HjemmelVisning } from '@/components/fagsystem/arbeidsplassen/visning/partials/HjemmelVisning'

export const ArbeidsplassenVisning = ({ data, loading, hjemmel }) => {
	if (loading) return <Loading label="Laster CV" />
	if (!data) {
		return null
	}

	return (
		<>
			<SubOverskrift label="Arbeidsplassen (CV)" iconKind="cv" />
			<Panel heading="CV-opplysninger">
				<JobboenskerVisning data={data.jobboensker} />
				<UtdanningVisning data={data.utdanning} />
				<FagbrevVisning data={data.fagbrev} />
				<ArbeidserfaringVisning data={data.arbeidserfaring} />
				<AnnenErfaringVisning data={data.annenErfaring} />
				<KompetanserVisning data={data.kompetanser} />
				<OffentligeGodkjenningerVisning data={data.offentligeGodkjenninger} />
				<AndreGodkjenningerVisning data={data.andreGodkjenninger} />
				<SpraakVisning data={data.spraak} />
				<FoererkortVisning data={data.foererkort} />
				<KursVisning data={data.kurs} />
				<SammendragVisning data={data.sammendrag} />
				<HjemmelVisning hjemmel={hjemmel} />
			</Panel>
		</>
	)
}
