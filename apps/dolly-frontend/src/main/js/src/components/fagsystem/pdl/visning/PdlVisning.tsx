import React from 'react'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import Loading from '~/components/ui/loading/Loading'
import { Telefonnummer } from '~/components/fagsystem/pdlf/visning/partials/Telefonnummer'
import { PdlPersonInfo } from '~/components/fagsystem/pdl/visning/partials/PdlPersonInfo'
import { IdentInfo } from '~/components/fagsystem/pdlf/visning/partials/Identinfo'
import { GeografiskTilknytning } from '~/components/fagsystem/pdlf/visning/partials/GeografiskTilknytning'
import { PdlNasjonalitet } from '~/components/fagsystem/pdl/visning/partials/PdlNasjonalitet'
import { PdlBoadresse } from '~/components/fagsystem/pdl/visning/partials/PdlBoadresse'
import { PdlFullmakt } from '~/components/fagsystem/pdl/visning/partials/PdlFullmakt'
import { PdlSikkerhetstiltak } from '~/components/fagsystem/pdl/visning/partials/PdlSikkerhetstiltak'
import {
	gjeldendeAdresse,
	PdlData,
} from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataVisning'

type PdlVisningProps = {
	pdlData: PdlData
	loading?: boolean
}

export const PdlVisning = ({ pdlData, loading }: PdlVisningProps) => {
	if (loading) return <Loading label="Laster PDL-data" />

	const data = pdlData.data
	if (!data || !data.hentPerson) {
		return null
	}

	return (
		<ErrorBoundary>
			<div>
				<PdlPersonInfo data={data.hentPerson} />
				<IdentInfo pdlResponse={data.hentIdenter} />
				<GeografiskTilknytning data={data.hentGeografiskTilknytning} />
				<PdlNasjonalitet data={data.hentPerson} />
				<PdlBoadresse data={gjeldendeAdresse(data.hentPerson.bostedsadresse)} />
				<Telefonnummer data={data.hentPerson.telefonnummer} />
				<PdlFullmakt data={data.hentPerson.fullmakt} />
				<PdlSikkerhetstiltak data={data.hentPerson.sikkerhetstiltak} />
			</div>
		</ErrorBoundary>
	)
}
