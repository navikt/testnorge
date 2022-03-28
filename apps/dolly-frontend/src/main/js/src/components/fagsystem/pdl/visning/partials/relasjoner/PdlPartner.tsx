import React from 'react'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { getSortedSivilstand } from '~/components/fagsystem/pdl/visning/partials/utils'
import { PersoninformasjonKodeverk } from '~/config/kodeverk'
import { ArrayHistorikk } from '~/components/ui/historikk/ArrayHistorikk'
import { Sivilstand } from '~/components/fagsystem/pdlf/PdlTypes'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'

type VisningProps = {
	data: Sivilstand
	idx?: number
}

type PdlPartnerProps = {
	data: Sivilstand[]
}

const Visning = ({ data, idx }: VisningProps) => {
	return (
		<div key={idx} className="person-visning_content">
			<TitleValue
				title="Forhold til partner (sivilstand)"
				kodeverk={PersoninformasjonKodeverk.Sivilstander}
				value={data.type}
				size="medium"
			/>
			<TitleValue title="Partnerident" value={data.relatertVedSivilstand} />
			<TitleValue title="Sivilstand fra dato" value={Formatters.formatDate(data.gyldigFraOgMed)} />
		</div>
	)
}

export const PdlPartner = ({ data }: PdlPartnerProps) => {
	const partnere = getSortedSivilstand(data)
	if (!partnere || partnere.length === 0) return null

	const gjeldendePartnere = partnere.filter((partner: Sivilstand) => !partner.metadata?.historisk)
	const historiskePartnere = partnere.filter((partner: Sivilstand) => partner.metadata?.historisk)

	return (
		<div>
			<SubOverskrift label="Sivilstand (partner)" iconKind="partner" />
			<ArrayHistorikk
				component={Visning}
				data={gjeldendePartnere}
				historiskData={historiskePartnere}
				header={''}
			/>
		</div>
	)
}
