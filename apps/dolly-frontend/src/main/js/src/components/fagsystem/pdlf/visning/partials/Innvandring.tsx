import React, { useEffect, useState } from 'react'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { AdresseKodeverk } from '~/config/kodeverk'
import Formatters from '~/utils/DataFormatter'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import {
	InnvandringValues,
	UtvandringValues,
} from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import _cloneDeep from 'lodash/cloneDeep'
import { initialInnvandring } from '~/components/fagsystem/pdlf/form/initialValues'
import _get from 'lodash/get'
import VisningRedigerbarConnector from '~/components/fagsystem/pdlf/visning/visningRedigerbar/VisningRedigerbarConnector'
import { PersonData } from '~/components/fagsystem/pdlf/PdlTypes'
import { getSisteDato } from '~/components/bestillingsveileder/utils'

type InnvandringTypes = {
	data: Array<InnvandringValues>
	utflyttingData?: Array<UtvandringValues>
	tmpPersoner?: Array<PersonData>
	ident?: string
	erPdlVisning?: boolean
}

type InnvandringLesTypes = {
	innvandringData: InnvandringValues
	idx: number
}

type InnvandringVisningTypes = {
	innvandringData: InnvandringValues
	idx: number
	sisteDato?: Date
	data: Array<InnvandringValues>
	tmpPersoner: Array<PersonData>
	ident: string
	erPdlVisning: boolean
	utflyttingData: Array<UtvandringValues>
}

export const getSisteDatoInnUtvandring = (
	innflyttingData: Array<InnvandringValues>,
	utflyttingData: Array<UtvandringValues>,
	tmpPersoner?: Array<PersonData>,
	ident?: string
) => {
	const tmpPerson = tmpPersoner?.hasOwnProperty(ident)

	const innflytting = tmpPerson ? _get(tmpPersoner, `${ident}.person.innflytting`) : innflyttingData
	const utflytting = tmpPerson ? _get(tmpPersoner, `${ident}.person.utflytting`) : utflyttingData

	let sisteInnflytting = getSisteDato(
		innflytting?.map((val: InnvandringValues) => new Date(val.innflyttingsdato))
	)
	let sisteUtflytting = getSisteDato(
		utflytting?.map((val: UtvandringValues) => new Date(val.utflyttingsdato))
	)
	return sisteInnflytting > sisteUtflytting ? sisteInnflytting : sisteUtflytting
}

const InnvandringLes = ({ innvandringData, idx }: InnvandringLesTypes) => {
	if (!innvandringData) {
		return null
	}
	return (
		<div className="person-visning_redigerbar" key={idx}>
			<TitleValue
				title="Fraflyttingsland"
				value={innvandringData.fraflyttingsland}
				kodeverk={AdresseKodeverk.InnvandretUtvandretLand}
			/>
			<TitleValue title="Fraflyttingssted" value={innvandringData.fraflyttingsstedIUtlandet} />
			<TitleValue
				title="Innflyttingsdato"
				value={Formatters.formatDate(innvandringData.innflyttingsdato)}
			/>
		</div>
	)
}

const InnvandringVisning = ({
	innvandringData,
	idx,
	sisteDato,
	data,
	tmpPersoner,
	ident,
	erPdlVisning,
	utflyttingData,
}: InnvandringVisningTypes) => {
	const initInnvandring = Object.assign(_.cloneDeep(initialInnvandring), data[idx])
	const initialValues = { innflytting: initInnvandring }

	const redigertInnvandringPdlf = _.get(tmpPersoner, `${ident}.person.innflytting`)?.find(
		(a: InnvandringValues) => a.id === innvandringData.id
	)
	const slettetInnvandringPdlf = tmpPersoner?.hasOwnProperty(ident) && !redigertInnvandringPdlf
	if (slettetInnvandringPdlf) {
		return <pre style={{ margin: '0' }}>Opplysning slettet</pre>
	}

	const innvandringValues = redigertInnvandringPdlf ? redigertInnvandringPdlf : innvandringData
	const redigertInnvandringValues = redigertInnvandringPdlf
		? {
				innflytting: Object.assign(_.cloneDeep(initialInnvandring), redigertInnvandringPdlf),
		  }
		: null
	return erPdlVisning ? (
		<InnvandringLes innvandringData={innvandringData} idx={idx} />
	) : (
		<VisningRedigerbarConnector
			dataVisning={<InnvandringLes innvandringData={innvandringValues} idx={idx} />}
			initialValues={initialValues}
			redigertAttributt={redigertInnvandringValues}
			path="innflytting"
			ident={ident}
			disableSlett={new Date(innvandringData.innflyttingsdato) < sisteDato}
			personFoerLeggTil={{
				pdldata: {
					person: {
						utflytting: utflyttingData,
					},
				},
			}}
		/>
	)
}

export const Innvandring = ({
	data,
	utflyttingData,
	tmpPersoner,
	ident,
	erPdlVisning = false,
}: InnvandringTypes) => {
	const [sisteDato, setSisteDato] = useState(
		getSisteDatoInnUtvandring(data, utflyttingData, tmpPersoner, ident)
	)

	useEffect(() => {
		if (data?.length > 0) {
			setSisteDato(getSisteDatoInnUtvandring(data, utflyttingData, tmpPersoner, ident))
		}
	}, [tmpPersoner])

	if (data.length < 1) {
		return null
	}

	return (
		<div className="person-visning_content" style={{ marginTop: '-20px' }}>
			<ErrorBoundary>
				<DollyFieldArray data={data} header="Innvandret" nested>
					{(innvandring: InnvandringValues, idx: number) => (
						<InnvandringVisning
							innvandringData={innvandring}
							idx={idx}
							sisteDato={sisteDato}
							data={data}
							utflyttingData={utflyttingData}
							tmpPersoner={tmpPersoner}
							ident={ident}
							erPdlVisning={erPdlVisning}
						/>
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
