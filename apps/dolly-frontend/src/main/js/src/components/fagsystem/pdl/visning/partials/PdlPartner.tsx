import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { Sivilstand } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import React from 'react'
import { getSortedSivilstand } from '~/components/fagsystem/pdl/visning/partials/utils'
import { PersoninformasjonKodeverk } from '~/config/kodeverk'

type VisningProps = {
	forhold: Sivilstand
	idx?: number
}

type PdlPartnerProps = {
	data: Sivilstand[]
}

const Visning = ({ forhold, idx }: VisningProps) => {
	return (
		<div key={idx} className="person-visning_content">
			<TitleValue
				title="Forhold til partner (sivilstand)"
				kodeverk={PersoninformasjonKodeverk.Sivilstander}
				value={forhold.type}
				size="medium"
			/>
			<TitleValue title="Partnerident" value={forhold.relatertVedSivilstand} />
			<TitleValue
				title="Sivilstand fra dato"
				value={Formatters.formatDate(forhold.gyldigFraOgMed)}
			/>
		</div>
	)
}

export const PdlPartner = ({ data }: PdlPartnerProps) => {
	const partnere = getSortedSivilstand(data)
	if (!partnere || partnere.length === 0) return null

	return (
		<div>
			{partnere.length > 1 ? (
				<ErrorBoundary>
					<DollyFieldArray header="Forhold" data={partnere} nested>
						{(forhold: Sivilstand, idx: number) => <Visning forhold={forhold} idx={idx} />}
					</DollyFieldArray>
				</ErrorBoundary>
			) : (
				<Visning forhold={partnere[0]} />
			)}
		</div>
	)
}
