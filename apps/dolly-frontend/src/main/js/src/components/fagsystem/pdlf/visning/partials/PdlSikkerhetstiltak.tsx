import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'

type Data = {
	data: SikkerhetstiltakData
}

type DataListe = {
	data: Array<SikkerhetstiltakData>
}

type SikkerhetstiltakData = {
	gyldigFraOgMed: Date
	gyldigTilOgMed: Date
	tiltakstype: string
	beskrivelse: string
	kontaktperson: Kontaktperson
}

type Kontaktperson = {
	personident: string
	enhet: string
}

export const Visning = ({ data }: Data) => {
	return (
		<>
			<div className="person-visning_content">
				<ErrorBoundary>
					<TitleValue
						title="Gyldig fra og med"
						value={Formatters.formatDate(data.gyldigFraOgMed)}
					/>
					<TitleValue
						title="Gyldig til og med"
						value={Formatters.formatDate(data.gyldigTilOgMed)}
					/>
					<TitleValue title="Tiltakstype" value={data.tiltakstype} />
					<TitleValue title="Beskrivelse" value={data.beskrivelse} />
					<TitleValue title="Kontaktperson ident" value={data.kontaktperson.personident} />
					<TitleValue title="Kontaktperson enhet" value={data.kontaktperson.enhet} />
				</ErrorBoundary>
			</div>
		</>
	)
}

export const PdlSikkerhetstiltak = ({ data }: DataListe) => {
	if (!data) return null
	return (
		<div>
			<SubOverskrift label="Sikkerhetstiltak" iconKind="sikkerhetstiltak" />
			{/* @ts-ignore */}
			<DollyFieldArray data={data} nested>
				{(sikkerhetstiltak: SikkerhetstiltakData) => <Visning data={sikkerhetstiltak} />}
			</DollyFieldArray>
		</div>
	)
}
