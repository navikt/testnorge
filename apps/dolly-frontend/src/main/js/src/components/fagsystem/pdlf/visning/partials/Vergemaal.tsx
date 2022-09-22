import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { RelatertPerson } from '~/components/fagsystem/pdlf/visning/partials/RelatertPerson'
import { Relasjon, Vergemaal } from '~/components/fagsystem/pdlf/PdlTypes'
import { VergemaalKodeverk } from '~/config/kodeverk'

type VergemaalData = {
	data: Array<Vergemaal>
	relasjoner: Array<Relasjon>
}

type VisningData = {
	data: Vergemaal
	relasjoner: Array<Relasjon>
}

export const Visning = ({ data, relasjoner }: VisningData) => {
	const retatertPersonIdent = data.vergeIdent
	const relasjon = relasjoner?.find((item) => item.relatertPerson?.ident === retatertPersonIdent)
	const harFullmektig = data.sakType === 'FRE'
	return (
		<>
			<ErrorBoundary>
				<div className="person-visning_content">
					<TitleValue
						title="Fylkesmannsembete"
						kodeverk={VergemaalKodeverk.Fylkesmannsembeter}
						value={data.vergemaalEmbete}
					/>
					<TitleValue title="Sakstype" kodeverk={VergemaalKodeverk.Sakstype} value={data.sakType} />
					<TitleValue
						title="Mandattype"
						kodeverk={VergemaalKodeverk.Mandattype}
						value={data.mandatType}
					/>
					<TitleValue title="Gyldig f.o.m." value={Formatters.formatDate(data.gyldigFraOgMed)} />
					<TitleValue title="Gyldig t.o.m." value={Formatters.formatDate(data.gyldigTilOgMed)} />
					{!relasjoner && (
						<TitleValue title={harFullmektig ? 'Fullmektig' : 'Verge'} value={data.vergeIdent} />
					)}
				</div>
				{relasjon && (
					<RelatertPerson
						data={relasjon.relatertPerson}
						tittel={harFullmektig ? 'Fullmektig' : 'Verge'}
					/>
				)}
			</ErrorBoundary>
		</>
	)
}

export const VergemaalVisning = ({ data, relasjoner }: VergemaalData) => {
	if (!data || data.length < 1) {
		return null
	}

	return (
		<div>
			<SubOverskrift label="Vergemål" iconKind="vergemaal" />

			<DollyFieldArray data={data} nested>
				{(vergemaal: Vergemaal) => (
					<Visning key={vergemaal.id} data={vergemaal} relasjoner={relasjoner} />
				)}
			</DollyFieldArray>
		</div>
	)
}
