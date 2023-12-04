import React from 'react'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { Vegadresse } from '@/components/fagsystem/pdlf/visning/partials/Vegadresse'
import { Matrikkeladresse } from '@/components/fagsystem/pdlf/visning/partials/Matrikkeladresse'
import { UtenlandskAdresse } from '@/components/fagsystem/pdlf/visning/partials/UtenlandskAdresse'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { showLabel } from '@/utils/DataFormatter'
import * as _ from 'lodash-es'
import { getInitialOppholdsadresse } from '@/components/fagsystem/pdlf/form/initialValues'
import { OppholdsadresseData } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import VisningRedigerbarConnector from '@/components/fagsystem/pdlf/visning/visningRedigerbar/VisningRedigerbarConnector'
import { OpplysningSlettet } from '@/components/fagsystem/pdlf/visning/visningRedigerbar/OpplysningSlettet'

type OppholdsadresseTypes = {
	data: Array<any>
	tmpPersoner?: Array<OppholdsadresseData>
	ident?: string
	erPdlVisning?: boolean
	identtype?: string
	erRedigerbar?: boolean
}

type AdresseTypes = {
	oppholdsadresseData: any
	idx: number
}

type OppholdsadresseVisningTypes = {
	oppholdsadresseData: any
	idx: number
	data: Array<any>
	tmpPersoner: Array<OppholdsadresseData>
	ident: string
	erPdlVisning: boolean
	identtype?: string
}

export const Adresse = ({ oppholdsadresseData, idx }: AdresseTypes) => {
	if (!oppholdsadresseData) {
		return null
	}

	return (
		<>
			{oppholdsadresseData.vegadresse && <Vegadresse adresse={oppholdsadresseData} idx={idx} />}
			{oppholdsadresseData.matrikkeladresse && (
				<Matrikkeladresse adresse={oppholdsadresseData} idx={idx} />
			)}
			{oppholdsadresseData.utenlandskAdresse && (
				<UtenlandskAdresse adresse={oppholdsadresseData} idx={idx} />
			)}
			{oppholdsadresseData.oppholdAnnetSted && (
				<div className="person-visning_content" key={idx}>
					<TitleValue
						title="Opphold annet sted"
						value={showLabel('oppholdAnnetSted', oppholdsadresseData.oppholdAnnetSted)}
					/>
				</div>
			)}
		</>
	)
}

const OppholdsadresseVisning = ({
	oppholdsadresseData,
	idx,
	data,
	tmpPersoner,
	ident,
	erPdlVisning,
	identtype,
}: OppholdsadresseVisningTypes) => {
	const initOppholdsadresse = Object.assign(_.cloneDeep(getInitialOppholdsadresse()), data[idx])
	const initialValues = { oppholdsadresse: initOppholdsadresse }

	const redigertOppholdsadressePdlf = _.get(tmpPersoner, `${ident}.person.oppholdsadresse`)?.find(
		(a: OppholdsadresseData) => a.id === oppholdsadresseData.id,
	)
	const slettetOppholdsadressePdlf =
		tmpPersoner?.hasOwnProperty(ident) && !redigertOppholdsadressePdlf
	if (slettetOppholdsadressePdlf) {
		return <OpplysningSlettet />
	}

	const oppholdsadresseValues = redigertOppholdsadressePdlf
		? redigertOppholdsadressePdlf
		: oppholdsadresseData
	const redigertOppholdsadresseValues = redigertOppholdsadressePdlf
		? {
				oppholdsadresse: Object.assign(
					_.cloneDeep(getInitialOppholdsadresse()),
					redigertOppholdsadressePdlf,
				),
		  }
		: null
	return erPdlVisning ? (
		<Adresse oppholdsadresseData={oppholdsadresseData} idx={idx} />
	) : (
		<VisningRedigerbarConnector
			dataVisning={<Adresse oppholdsadresseData={oppholdsadresseValues} idx={idx} />}
			initialValues={initialValues}
			redigertAttributt={redigertOppholdsadresseValues}
			path="oppholdsadresse"
			ident={ident}
			identtype={identtype}
		/>
	)
}

export const Oppholdsadresse = ({
	data,
	tmpPersoner,
	ident,
	erPdlVisning = false,
	identtype,
	erRedigerbar = true,
}: OppholdsadresseTypes) => {
	if (!data || data.length === 0) {
		return null
	}

	return (
		<>
			<SubOverskrift label="Oppholdsadresse" iconKind="adresse" />
			<div className="person-visning_content">
				<ErrorBoundary>
					<DollyFieldArray data={data} header="" nested>
						{(adresse: any, idx: number) =>
							erRedigerbar ? (
								<OppholdsadresseVisning
									oppholdsadresseData={adresse}
									idx={idx}
									data={data}
									tmpPersoner={tmpPersoner}
									ident={ident}
									erPdlVisning={erPdlVisning}
								identtype={identtype}/>
							) : (
								<Adresse oppholdsadresseData={adresse} idx={idx} />
							)
						}
					</DollyFieldArray>
				</ErrorBoundary>
			</div>
		</>
	)
}
