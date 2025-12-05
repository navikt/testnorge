import React, { useEffect, useState } from 'react'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { AdresseKodeverk } from '@/config/kodeverk'
import { formatDate } from '@/utils/DataFormatter'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import {
	InnvandringValues,
	UtvandringValues,
} from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import * as _ from 'lodash-es'
import { PersonData } from '@/components/fagsystem/pdlf/PdlTypes'
import { initialUtvandring } from '@/components/fagsystem/pdlf/form/initialValues'
import { VisningRedigerbar } from "@/components/fagsystem/pdlf/visning/visningRedigerbar/VisningRedigerbar"
import { getSisteDatoInnUtvandring } from '@/components/fagsystem/pdlf/visning/partials/Innvandring'
import { OpplysningSlettet } from '@/components/fagsystem/pdlf/visning/visningRedigerbar/OpplysningSlettet'

type UtvandringTypes = {
	data: Array<UtvandringValues>
	innflyttingData?: Array<InnvandringValues>
	tmpPersoner?: Array<PersonData>
	ident?: string
	erPdlVisning?: boolean
	erRedigerbar?: boolean
}

type UtvandringLesTypes = {
	utvandringData: UtvandringValues
	idx: number
}

type UtvandringVisningTypes = {
	utvandringData: UtvandringValues
	idx: number
	sisteDato?: Date
	data: Array<UtvandringValues>
	innflyttingData?: Array<InnvandringValues>
	tmpPersoner?: Array<PersonData>
	ident?: string
	erPdlVisning?: boolean
}

const UtvandringLes = ({ utvandringData, idx }: UtvandringLesTypes) => {
	if (!utvandringData) {
		return null
	}
	return (
		<div className="person-visning_redigerbar" key={idx}>
			<TitleValue
				title="Tilflyttingsland"
				value={utvandringData.tilflyttingsland}
				kodeverk={AdresseKodeverk.InnvandretUtvandretLand}
			/>
			<TitleValue title="Tilflyttingssted" value={utvandringData.tilflyttingsstedIUtlandet} />
			<TitleValue title="Utflyttingsdato" value={formatDate(utvandringData.utflyttingsdato)} />
		</div>
	)
}

const UtvandringVisning = ({
	utvandringData,
	idx,
	data,
	tmpPersoner,
	ident,
	erPdlVisning,
}: UtvandringVisningTypes) => {
	const initUtvandring = Object.assign(_.cloneDeep(initialUtvandring), data[idx])
	const initialValues = { utflytting: initUtvandring }

	const redigertUtvandringPdlf = _.get(tmpPersoner, `${ident}.person.utflytting`)?.find(
		(a: UtvandringValues) => a.id === utvandringData.id,
	)
	const slettetUtvandringPdlf = tmpPersoner?.hasOwnProperty(ident) && !redigertUtvandringPdlf
	if (slettetUtvandringPdlf) {
		return <OpplysningSlettet />
	}

	const utvandringValues = redigertUtvandringPdlf ? redigertUtvandringPdlf : utvandringData
	const redigertUtvandringValues = redigertUtvandringPdlf
		? {
				utflytting: Object.assign(_.cloneDeep(initialUtvandring), redigertUtvandringPdlf),
			}
		: null
	return erPdlVisning ? (
		<UtvandringLes utvandringData={utvandringData} idx={idx} />
	) : (
		<VisningRedigerbar
			dataVisning={<UtvandringLes utvandringData={utvandringValues} idx={idx} />}
			initialValues={initialValues}
			redigertAttributt={redigertUtvandringValues}
			path="utflytting"
			ident={ident}
		/>
	)
}

export const Utvandring = ({
	data,
	innflyttingData,
	tmpPersoner,
	ident,
	erPdlVisning,
	erRedigerbar = true,
}: UtvandringTypes) => {
	const [sisteDato, setSisteDato] = useState(
		getSisteDatoInnUtvandring(innflyttingData, data, tmpPersoner, ident),
	)

	useEffect(() => {
		if (data?.length > 0) {
			setSisteDato(getSisteDatoInnUtvandring(innflyttingData, data, tmpPersoner, ident))
		}
	}, [tmpPersoner])

	if (data.length < 1) {
		return null
	}

	return (
		<div className="person-visning_content" style={{ marginTop: '-20px' }}>
			<ErrorBoundary>
				<DollyFieldArray data={data} header={'Utvandret'} nested>
					{(utvandring: UtvandringValues, idx: number) =>
						erRedigerbar ? (
							<UtvandringVisning
								utvandringData={utvandring}
								idx={idx}
								sisteDato={sisteDato}
								data={data}
								innflyttingData={innflyttingData}
								tmpPersoner={tmpPersoner}
								ident={ident}
								erPdlVisning={erPdlVisning}
							/>
						) : (
							<UtvandringLes utvandringData={utvandring} idx={idx} />
						)
					}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
