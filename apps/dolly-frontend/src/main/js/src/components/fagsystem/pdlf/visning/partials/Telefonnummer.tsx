import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { TelefonData } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import * as _ from 'lodash-es'
import { VisningRedigerbarSamlet } from '@/components/fagsystem/pdlf/visning/visningRedigerbar/VisningRedigerbarSamlet'

type DataListe = {
	data: Array<TelefonData>
	tmpPersoner: any
	ident: string
	erPdlVisning?: boolean
	erRedigerbar?: boolean
}

type TelefonnummerTypes = {
	telefonnummerData: TelefonData
	idx: number
}

export const TelefonnummerLes = ({ telefonnummerData, idx }: TelefonnummerTypes) => {
	if (!telefonnummerData) {
		return null
	}

	return (
		<div className="person-visning_redigerbar" key={idx}>
			<TitleValue
				title="Telefonnummer"
				value={`${telefonnummerData.landskode} ${telefonnummerData.nummer}`}
			/>
			<TitleValue title="Prioritet" value={telefonnummerData.prioritet} />
			<TitleValue
				title="Master"
				value={telefonnummerData.metadata?.master || telefonnummerData.master}
			/>
		</div>
	)
}

export const Telefonnummer = ({
	data,
	tmpPersoner,
	ident,
	erPdlVisning = false,
	erRedigerbar = true,
}: DataListe) => {
	if (!data || data.length === 0) {
		return null
	}
	const initialValues = { telefonnummer: data }

	const redigertTelefonnummerPdlf = _.get(tmpPersoner, `${ident}.person.telefonnummer`)
	const redigertTelefonnummerValues = redigertTelefonnummerPdlf && {
		telefonnummer: _.get(tmpPersoner, `${ident}.person.telefonnummer`),
	}

	const slettetTelefonnummerPdlf = tmpPersoner?.hasOwnProperty(ident) && !redigertTelefonnummerPdlf

	const disableSlett = (telefonData: Array<TelefonData>) => {
		if (telefonData.filter((obj) => obj !== null).length < 2) {
			return null
		} else {
			return telefonData.map((tlf) => tlf?.id).indexOf(1)
		}
	}

	return (
		<div>
			<SubOverskrift label="Telefonnummer" iconKind="telephone" />
			<div className="person-visning_content">
				{erPdlVisning || !erRedigerbar ? (
					<ErrorBoundary>
						<DollyFieldArray data={data} header="" nested>
							{(item: TelefonData, idx: number) => (
								<TelefonnummerLes telefonnummerData={item} idx={idx} />
							)}
						</DollyFieldArray>
					</ErrorBoundary>
				) : (
					<VisningRedigerbarSamlet
						initialValues={initialValues}
						redigertAttributt={redigertTelefonnummerValues}
						path="telefonnummer"
						ident={ident}
						alleSlettet={slettetTelefonnummerPdlf}
						disableSlett={disableSlett}
					/>
				)}
			</div>
		</div>
	)
}
