import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import Formatters from '@/utils/DataFormatter'
import { RelatertPerson } from '@/components/fagsystem/pdlf/visning/partials/RelatertPerson'
import { Relasjon, SivilstandData } from '@/components/fagsystem/pdlf/PdlTypes'

type SivilstandTypes = {
	data: Array<SivilstandData>
	relasjoner: Array<Relasjon>
}

type VisningData = {
	data: SivilstandData
	relasjoner: Array<Relasjon>
}

const SivilstandLes = ({ sivilstandData, idx }) => {
	if (!sivilstandData) {
		return null
	}
	return (
		<div className="person-visning_redigerbar" key={idx}>
			<ErrorBoundary>
				<div className="person-visning_content">
					<TitleValue
						title="Type"
						value={Formatters.showLabel('sivilstandType', sivilstandData.type)}
					/>
					<TitleValue
						title="Gyldig fra og med"
						value={
							Formatters.formatDate(sivilstandData.sivilstandsdato) ||
							Formatters.formatDate(sivilstandData.gyldigFraOgMed)
						}
					/>
					<TitleValue
						title="Bekreftelsesdato"
						value={Formatters.formatDate(sivilstandData.bekreftelsesdato)}
					/>
					{!relasjoner && (
						<TitleValue title="Relatert person" value={sivilstandData.relatertVedSivilstand} />
					)}
				</div>
				{relasjon && (
					<RelatertPerson
						data={relasjon.relatertPerson}
						tittel={sivilstandData.type === 'SAMBOER' ? 'Samboer' : 'Ektefelle/partner'}
					/>
				)}
			</ErrorBoundary>
		</div>
	)
}

export const SivilstandVisning = ({ data, relasjoner }: VisningData) => {
	const relatertPersonIdent = data.relatertVedSivilstand
	const relasjon = relasjoner?.find((item) => item.relatertPerson?.ident === relatertPersonIdent)

	return

	return (
		<>
			{/*<ErrorBoundary>*/}
			{/*	<div className="person-visning_content">*/}
			{/*		<TitleValue title="Type" value={Formatters.showLabel('sivilstandType', data.type)} />*/}
			{/*		<TitleValue*/}
			{/*			title="Gyldig fra og med"*/}
			{/*			value={*/}
			{/*				Formatters.formatDate(data.sivilstandsdato) ||*/}
			{/*				Formatters.formatDate(data.gyldigFraOgMed)*/}
			{/*			}*/}
			{/*		/>*/}
			{/*		<TitleValue*/}
			{/*			title="Bekreftelsesdato"*/}
			{/*			value={Formatters.formatDate(data.bekreftelsesdato)}*/}
			{/*		/>*/}
			{/*		{!relasjoner && <TitleValue title="Relatert person" value={data.relatertVedSivilstand} />}*/}
			{/*	</div>*/}
			{/*	{relasjon && (*/}
			{/*		<RelatertPerson*/}
			{/*			data={relasjon.relatertPerson}*/}
			{/*			tittel={data.type === 'SAMBOER' ? 'Samboer' : 'Ektefelle/partner'}*/}
			{/*		/>*/}
			{/*	)}*/}
			{/*</ErrorBoundary>*/}
		</>
	)
}

export const Sivilstand = ({ data, relasjoner }: SivilstandTypes) => {
	if (!data || data.length < 1) {
		return null
	}
	return (
		<div>
			<SubOverskrift label="Sivilstand (partner)" iconKind="partner" />

			<DollyFieldArray data={data} nested>
				{(sivilstand: SivilstandData) => (
					<SivilstandVisning key={sivilstand.id} data={sivilstand} relasjoner={relasjoner} />
				)}
			</DollyFieldArray>
		</div>
	)
}
