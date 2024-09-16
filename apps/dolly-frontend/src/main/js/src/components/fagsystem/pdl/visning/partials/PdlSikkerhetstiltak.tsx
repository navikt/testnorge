import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate } from '@/utils/DataFormatter'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { SikkerhetstiltakData } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import _ from 'lodash'
import { initialSikkerhetstiltak } from '@/components/fagsystem/pdlf/form/initialValues'
import { Person } from '@/components/fagsystem/pdlf/PdlTypes'
import { OpplysningSlettet } from '@/components/fagsystem/pdlf/visning/visningRedigerbar/OpplysningSlettet'
import VisningRedigerbarConnector from '@/components/fagsystem/pdlf/visning/visningRedigerbar/VisningRedigerbarConnector'
import React from 'react'

type Data = {
	data: SikkerhetstiltakData
}

type DataListe = {
	data: Array<SikkerhetstiltakData>
}

const SikkerhetstiltakLes = ({ data, idx }: Data) => {
	return (
		<div className="person-visning_content" key={idx}>
			<ErrorBoundary>
				<TitleValue title="Gyldig fra og med" value={formatDate(data.gyldigFraOgMed)} />
				<TitleValue title="Gyldig til og med" value={formatDate(data.gyldigTilOgMed)} />
				<TitleValue title="Tiltakstype" value={data.tiltakstype} />
				<TitleValue title="Beskrivelse" value={data.beskrivelse} />
				<TitleValue title="Kontaktperson ident" value={data.kontaktperson.personident} />
				<TitleValue title="NAV kontor" value={data.kontaktperson.enhet} />
			</ErrorBoundary>
		</div>
	)
}

export const SikkerhetstiltakVisningRedigerbar = ({
	sikkerhetstiltak,
	idx,
	tmpPersoner,
	data,
	erPdlVisning,
	ident,
	master,
}) => {
	const initSikkerhetstiltak = Object.assign(_.cloneDeep(initialSikkerhetstiltak), data[idx])
	const initialValues = { sikkerhetstiltak: initSikkerhetstiltak }

	const redigertSikkerhetstiltakPdlf = _.get(tmpPersoner, `${ident}.person.sikkerhetstiltak`)?.find(
		(a: Person) => a.id === sikkerhetstiltak.id,
	)
	const slettetSikkerhetstiltakPdlf =
		tmpPersoner?.hasOwnProperty(ident) && !redigertSikkerhetstiltakPdlf
	if (slettetSikkerhetstiltakPdlf) {
		return <OpplysningSlettet />
	}

	const sikkerhetstiltakValues = redigertSikkerhetstiltakPdlf
		? redigertSikkerhetstiltakPdlf
		: sikkerhetstiltak
	const redigertSikkerhetstiltaklValues = redigertSikkerhetstiltakPdlf
		? {
				sikkerhetstiltak: Object.assign(
					_.cloneDeep(initSikkerhetstiltak),
					redigertSikkerhetstiltakPdlf,
				),
			}
		: null

	return erPdlVisning ? (
		<SikkerhetstiltakLes data={sikkerhetstiltak} idx={idx} />
	) : (
		<VisningRedigerbarConnector
			dataVisning={<SikkerhetstiltakLes data={sikkerhetstiltakValues} idx={idx} />}
			initialValues={initialValues}
			redigertAttributt={redigertSikkerhetstiltaklValues}
			path="sikkerhetstiltak"
			ident={ident}
			master={master}
		/>
	)
}

export const PdlSikkerhetstiltak = ({
	data,
	pdlfData,
	tmpPersoner,
	ident,
	erPdlVisning,
}: DataListe) => {
	if (!data || data.length === 0) {
		return null
	}

	return (
		<div>
			<SubOverskrift label="Sikkerhetstiltak" iconKind="sikkerhetstiltak" />
			{/* @ts-ignore */}
			<DollyFieldArray data={data} nested>
				{(sikkerhetstiltak: SikkerhetstiltakData, idx: number) => {
					const master = sikkerhetstiltak?.metadata?.master
					const pdlfElement = pdlfData?.find(
						(element) => element.hendelseId === sikkerhetstiltak?.metadata?.opplysningsId,
					)
					if (!erPdlVisning && master !== 'FREG') {
						return (
							<SikkerhetstiltakVisningRedigerbar
								sikkerhetstiltak={pdlfElement || sikkerhetstiltak}
								idx={idx}
								data={pdlfData || data}
								tmpPersoner={tmpPersoner}
								ident={ident}
								erPdlVisning={erPdlVisning}
								master={master}
							/>
						)
					}
					return <SikkerhetstiltakLes data={sikkerhetstiltak} />
				}}
			</DollyFieldArray>
		</div>
	)
}
