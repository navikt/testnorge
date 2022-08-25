import React from 'react'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { AdresseKodeverk } from '~/config/kodeverk'
import Formatters from '~/utils/DataFormatter'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import {
	InnvandringValues,
	UtvandringValues,
} from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { PersonData } from '~/components/fagsystem/pdlf/PdlTypes'
import _cloneDeep from 'lodash/cloneDeep'
import { initialUtvandring } from '~/components/fagsystem/pdlf/form/initialValues'
import _get from 'lodash/get'
import VisningRedigerbarConnector from '~/components/fagsystem/pdlf/visning/VisningRedigerbarConnector'
import { getSisteDatoInnUtvandring } from '~/components/fagsystem/pdlf/visning/partials/Innvandring'
import { utflytting } from '~/components/fagsystem/pdlf/form/validation'

type UtvandringTypes = {
	data: Array<UtvandringValues>
	innflyttingData?: Array<InnvandringValues>
	tmpPersoner?: Array<PersonData>
	ident?: string
	erPdlVisning?: boolean
}

type UtvandringVisningTypes = {
	utvandringData: UtvandringValues
	idx: number
	sisteDato?: Date
}

export const Utvandring = ({
	data,
	innflyttingData,
	tmpPersoner,
	ident,
	erPdlVisning,
}: UtvandringTypes) => {
	if (data.length < 1) {
		return null
	}

	const UtvandringLes = ({ utvandringData, idx }: UtvandringVisningTypes) => {
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
				<TitleValue
					title="Utflyttingsdato"
					value={Formatters.formatDate(utvandringData.utflyttingsdato)}
				/>
			</div>
		)
	}

	const UtvandringVisning = ({ utvandringData, idx, sisteDato }: UtvandringVisningTypes) => {
		const initUtvandring = Object.assign(_cloneDeep(initialUtvandring), data[idx])
		const initialValues = { utflytting: initUtvandring }

		const redigertUtvandringPdlf = _get(tmpPersoner, `${ident}.person.utflytting`)?.find(
			(a: UtvandringValues) => a.id === utvandringData.id
		)
		const slettetUtvandringPdlf = tmpPersoner?.hasOwnProperty(ident) && !redigertUtvandringPdlf
		if (slettetUtvandringPdlf) {
			return <pre style={{ margin: '0' }}>Opplysning slettet</pre>
		}

		const utvandringValues = redigertUtvandringPdlf ? redigertUtvandringPdlf : utvandringData
		const redigertUtvandringValues = redigertUtvandringPdlf
			? {
					utflytting: Object.assign(_cloneDeep(initialUtvandring), redigertUtvandringPdlf),
			  }
			: null
		return erPdlVisning ? (
			<UtvandringLes utvandringData={utvandringData} idx={idx} />
		) : (
			<VisningRedigerbarConnector
				dataVisning={<UtvandringLes utvandringData={utvandringValues} idx={idx} />}
				initialValues={initialValues}
				redigertAttributt={redigertUtvandringValues}
				path="utflytting"
				ident={ident}
				slettDisabled={new Date(utvandringData.utflyttingsdato) < sisteDato}
			/>
		)
	}

	const sisteDatoForVandring = getSisteDatoInnUtvandring(innflyttingData, data)

	return (
		<div className="person-visning_content" style={{ marginTop: '-20px' }}>
			<ErrorBoundary>
				<DollyFieldArray data={data} header={'Utvandret'} nested>
					{(utvandring: UtvandringValues, idx: number) => (
						<UtvandringVisning
							utvandringData={utvandring}
							idx={idx}
							sisteDato={sisteDatoForVandring}
						/>
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
