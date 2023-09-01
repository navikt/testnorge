import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate } from '@/utils/DataFormatter'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { SikkerhetstiltakData } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'

type Data = {
	data: SikkerhetstiltakData
}

type DataListe = {
	data: Array<SikkerhetstiltakData>
}

export const Visning = ({ data }: Data) => {
	return (
		<>
			<div className="person-visning_content">
				<ErrorBoundary>
					<TitleValue title="Gyldig fra og med" value={formatDate(data.gyldigFraOgMed)} />
					<TitleValue title="Gyldig til og med" value={formatDate(data.gyldigTilOgMed)} />
					<TitleValue title="Tiltakstype" value={data.tiltakstype} />
					<TitleValue title="Beskrivelse" value={data.beskrivelse} />
					<TitleValue title="Kontaktperson ident" value={data.kontaktperson.personident} />
					<TitleValue title="NAV kontor" value={data.kontaktperson.enhet} />
				</ErrorBoundary>
			</div>
		</>
	)
}

export const PdlSikkerhetstiltak = ({ data }: DataListe) => {
	if (!data || data.length === 0) {
		return null
	}
	return (
		<div>
			<SubOverskrift label="Sikkerhetstiltak" iconKind="designsystem-sikkerhetstiltak" />
			{/* @ts-ignore */}
			<DollyFieldArray data={data} nested>
				{(sikkerhetstiltak: SikkerhetstiltakData) => <Visning data={sikkerhetstiltak} />}
			</DollyFieldArray>
		</div>
	)
}
