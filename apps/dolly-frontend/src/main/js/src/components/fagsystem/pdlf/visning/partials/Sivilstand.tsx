import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { RelatertPerson } from '~/components/fagsystem/pdlf/visning/partials/RelatertPerson'

export const Visning = ({ data, relasjoner }) => {
	const retatertPersonIdent = data.relatertVedSivilstand
	const relasjon = relasjoner?.find(
		(relasjon) => relasjon.relatertPerson?.ident === retatertPersonIdent
	)

	return (
		<>
			<ErrorBoundary>
				<div className="person-visning_content">
					<TitleValue title="Type" value={Formatters.showLabel('sivilstandType', data.type)} />
					<TitleValue
						title="Gyldig fra og med"
						value={
							Formatters.formatDate(data.sivilstandsdato) ||
							Formatters.formatDate(data.gyldigFraOgMed)
						}
					/>
					<TitleValue
						title="Bekreftelsesdato"
						value={Formatters.formatDate(data.bekreftelsesdato)}
					/>
					{!relasjoner && <TitleValue title="Relatert person" value={data.relatertVedSivilstand} />}
				</div>
				{relasjon && <RelatertPerson data={relasjon.relatertPerson} tittel="Ektefelle/partner" />}
			</ErrorBoundary>
		</>
	)
}

export const Sivilstand = ({ data, relasjoner }) => {
	if (!data || data.length < 1) return null

	return (
		<div>
			<SubOverskrift label="Sivilstand (partner)" iconKind="partner" />

			<DollyFieldArray data={data} nested>
				{(sivilstand) => <Visning key={sivilstand.id} data={sivilstand} relasjoner={relasjoner} />}
			</DollyFieldArray>
		</div>
	)
}
