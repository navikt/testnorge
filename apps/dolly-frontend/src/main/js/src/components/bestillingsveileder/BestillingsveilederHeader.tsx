import React from 'react'
import { Header } from '@/components/ui/header/Header'
import { arrayToString } from '@/utils/DataFormatter'
import { getLeggTilIdent } from '@/components/bestillingsveileder/utils'
import { useGruppeById } from '@/utils/hooks/useGruppe'
import { UseFormReturn } from 'react-hook-form/dist/types'
import { BestillingsveilederContextType } from '@/components/bestillingsveileder/BestillingsveilederContext'

export const BestillingsveilederHeader = ({
	context,
	formMethods,
}: {
	context: BestillingsveilederContextType
	formMethods: UseFormReturn
}) => {
	const formGruppeId = formMethods.watch('gruppeId') || ''
	const { gruppe } = useGruppeById(formGruppeId || context.gruppeId || context.gruppe?.id)
	const formId2032 = formMethods.watch('pdldata.opprettNyPerson.id2032')

	const ident = getLeggTilIdent(context.personFoerLeggTil, context.identMaster) || ''
	const importFra = context.is?.leggTil && context.identMaster === 'PDL' ? 'Test-Norge' : ''

	const getIdenttype = () => {
		if (!context.identtype) return undefined
		const id2032Value = formId2032 ?? context.id2032
		if (id2032Value) return `${context.identtype} (id 2032)`
		return context.identtype
	}

	if (
		context.is?.nyOrganisasjon ||
		context.is?.nyStandardOrganisasjon ||
		context.is?.nyOrganisasjonFraMal
	) {
		const titleValue = context.is?.nyStandardOrganisasjon ? 'Standard organisasjon' : 'Organisasjon'
		return (
			<Header icon="organisasjon" iconClassName="org">
				<div className="flexbox">
					<Header.TitleValue title="Opprett ny" value={titleValue} />
					{context.is?.nyOrganisasjonFraMal && (
						<Header.TitleValue title="Basert på mal" value={context.mal?.malNavn || ''} />
					)}
				</div>
			</Header>
		)
	}

	return (
		<Header>
			<div className="flexbox">
				<Header.TitleValue
					title="Antall"
					value={`${context.antall ?? 0} ${
						context.antall && context.antall > 1 ? 'personer' : 'person'
					}`}
				/>
				{!context.is?.opprettFraIdenter && !context.is?.leggTilPaaGruppe && (
					<Header.TitleValue title="Identtype" value={getIdenttype() ?? ''} />
				)}
				{context.is?.opprettFraIdenter && (
					<Header.TitleValue
						title="Opprett fra eksisterende personer"
						value={arrayToString(context.opprettFraIdenter) || ''}
					/>
				)}
				{context.mal?.malNavn && (
					<Header.TitleValue title="Basert på mal" value={context.mal?.malNavn || ''} />
				)}
				{context.is?.importTestnorge && (
					<Header.TitleValue title="Importer fra" value="Test-Norge" />
				)}
				{context.is?.leggTil && (
					<Header.TitleValue title="Legg til/endre på person" value={ident} />
				)}
				{context.is?.leggTilPaaGruppe && (
					<Header.TitleValue
						title="Legg til på / endre alle personer"
						value={`Gruppe #${formGruppeId || context.gruppeId || ''} - ${gruppe?.navn || ''}`}
					/>
				)}
				{importFra && <Header.TitleValue title="Importert fra" value={importFra} />}
				{!context.is?.leggTilPaaGruppe && (
					<Header.TitleValue title="Gruppe" value={gruppe?.navn || ''} />
				)}
			</div>
		</Header>
	)
}
