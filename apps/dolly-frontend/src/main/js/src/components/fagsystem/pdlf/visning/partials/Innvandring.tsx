import React from 'react'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { AdresseKodeverk } from '~/config/kodeverk'
import Formatters from '~/utils/DataFormatter'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { InnvandringValues } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import _cloneDeep from 'lodash/cloneDeep'
import { initialInnvandring } from '~/components/fagsystem/pdlf/form/initialValues'
import _get from 'lodash/get'
import VisningRedigerbarConnector from '~/components/fagsystem/pdlf/visning/VisningRedigerbarConnector'
import { PersonData } from '~/components/fagsystem/pdlf/PdlTypes'

type InnvandringTypes = {
	data: Array<InnvandringValues>
	tmpPersoner?: Array<PersonData>
	ident?: string
	erPdlVisning?: boolean
}

type InnvandringVisningTypes = {
	innvandringData: InnvandringValues
	idx: number
}

export const Innvandring = ({
	data,
	tmpPersoner,
	ident,
	erPdlVisning = false,
}: InnvandringTypes) => {
	if (data.length < 1) return null

	const InnvandringLes = ({ innvandringData, idx }: InnvandringVisningTypes) => {
		if (!innvandringData) return null
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

	const InnvandringVisning = ({ innvandringData, idx }: InnvandringVisningTypes) => {
		const initInnvandring = Object.assign(_cloneDeep(initialInnvandring), data[idx])
		const initialValues = { innflytting: initInnvandring }

		const redigertInnvandringPdlf = _get(tmpPersoner, `${ident}.person.innflytting`)?.find(
			(a: InnvandringValues) => a.id === innvandringData.id
		)
		const slettetInnvandringPdlf = tmpPersoner?.hasOwnProperty(ident) && !redigertInnvandringPdlf
		if (slettetInnvandringPdlf) return <pre style={{ margin: '0' }}>Opplysning slettet</pre>

		const innvandringValues = redigertInnvandringPdlf ? redigertInnvandringPdlf : innvandringData
		const redigertInnvvandringValues = redigertInnvandringPdlf
			? {
					innflytting: Object.assign(_cloneDeep(initialInnvandring), redigertInnvandringPdlf),
			  }
			: null
		return erPdlVisning ? (
			<InnvandringLes innvandringData={innvandringData} idx={idx} />
		) : (
			<VisningRedigerbarConnector
				dataVisning={<InnvandringLes innvandringData={innvandringValues} idx={idx} />}
				initialValues={initialValues}
				redigertAttributt={redigertInnvvandringValues}
				path="innflytting"
				ident={ident}
			/>
		)
	}

	return (
		<div className="person-visning_content" style={{ marginTop: '-20px' }}>
			<ErrorBoundary>
				<DollyFieldArray data={data} header="Innvandret" nested>
					{(innvandring: InnvandringValues, idx: number) => (
						<InnvandringVisning innvandringData={innvandring} idx={idx} />
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
