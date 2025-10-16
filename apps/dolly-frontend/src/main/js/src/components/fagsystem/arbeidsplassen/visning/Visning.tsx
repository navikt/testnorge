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
import styled from 'styled-components'
import { PadlockLockedFillIcon } from '@navikt/aksel-icons'
import { BodyLong } from '@navikt/ds-react'
import StyledAlert from '@/components/ui/alert/StyledAlert'

const StyledCVVisning = styled.div`
	margin-bottom: 20px;
`

const ForbiddenVisning = styled.div`
	align-items: center;
	margin-bottom: 20px;

	&& {
		p {
			margin-left: 10px;
		}
	}
`

export const ArbeidsplassenVisning = ({ data, loading, error, hjemmel }) => {
	if (loading && !data && !error) return <Loading label="Laster CV" />

	if (error?.status === 403)
		return (
			<>
				<SubOverskrift label="Nav CV" iconKind="cv" isWarning />
				<ForbiddenVisning className="flexbox">
					<PadlockLockedFillIcon color={'#C77300'} fontSize={'2rem'} />
					<BodyLong size={'small'}>{error?.message}</BodyLong>
				</ForbiddenVisning>
			</>
		)

	if (error) {
		return (
			<>
				<SubOverskrift label="Nav CV" iconKind="cv" isWarning />
				<StyledAlert variant={'warning'} size={'small'} inline>
					{error?.message ?? 'Fant ikke CV-data på person'}
				</StyledAlert>
			</>
		)
	}

	if (!data) {
		return null
	}

	return (
		<StyledCVVisning>
			<SubOverskrift label="Nav CV" iconKind="cv" />
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
		</StyledCVVisning>
	)
}
