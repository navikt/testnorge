import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate } from '@/utils/DataFormatter'
import { getSortedSivilstand } from '@/components/fagsystem/pdl/visning/partials/utils'
import { PersoninformasjonKodeverk } from '@/config/kodeverk'
import { ArrayHistorikk } from '@/components/ui/historikk/ArrayHistorikk'
import { Sivilstand } from '@/components/fagsystem/pdlf/PdlTypes'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'

type VisningProps = {
	data: Sivilstand
	idx?: number
}

type PdlPartnerProps = {
	data: Sivilstand[]
}

const Visning = ({ data, idx }: VisningProps) => {
	const harPartner = data.type !== 'UGIFT'
	return (
		<div key={idx} className="person-visning_content">
			<TitleValue
				title={harPartner ? 'Forhold til partner (sivilstand)' : 'Sivilstand'}
				kodeverk={PersoninformasjonKodeverk.Sivilstander}
				value={data.type}
				size="medium"
			/>
			{harPartner && (
				<TitleValue title="Partnerident" value={data.relatertVedSivilstand} visKopier />
			)}
			<TitleValue title="Sivilstand fra dato" value={formatDate(data.gyldigFraOgMed)} />
		</div>
	)
}

export const PdlPartner = ({ data }: PdlPartnerProps) => {
	const partnere = getSortedSivilstand(data)
	if (!partnere || partnere.length === 0) {
		return null
	}

	const gjeldendePartnere = partnere.filter((partner: Sivilstand) => !partner.metadata?.historisk)
	const historiskePartnere = partnere.filter((partner: Sivilstand) => partner.metadata?.historisk)

	return (
		<div>
			<SubOverskrift label="Sivilstand (partner)" iconKind="designsystem-partner" />
			<ArrayHistorikk
				component={Visning}
				data={gjeldendePartnere}
				historiskData={historiskePartnere}
				header={''}
			/>
		</div>
	)
}
