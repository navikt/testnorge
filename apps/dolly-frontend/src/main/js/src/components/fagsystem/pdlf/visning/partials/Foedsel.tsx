import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { formatDate } from '@/utils/DataFormatter'
import KodeverkConnector from '@/components/kodeverk/KodeverkConnector'
import * as _ from 'lodash-es'
import {
	Kodeverk,
	KodeverkValues,
} from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { AdresseKodeverk } from '@/config/kodeverk'
import {
	FoedestedData,
	FoedselData,
	FoedselsdatoData,
	Person,
	PersonData,
} from '@/components/fagsystem/pdlf/PdlTypes'
import {
	getInitialFoedested,
	getInitialFoedsel,
	getInitialFoedselsdato,
} from '@/components/fagsystem/pdlf/form/initialValues'
import { VisningRedigerbar } from "@/components/fagsystem/pdlf/visning/visningRedigerbar/VisningRedigerbar"
import { OpplysningSlettet } from '@/components/fagsystem/pdlf/visning/visningRedigerbar/OpplysningSlettet'

type FoedselTypes = {
	data: PersonData
	tmpPersoner?: Array<FoedselData>
	ident?: string
	erPdlVisning?: boolean
	erRedigerbar?: boolean
}

type FoedselLesTypes = {
	foedsel: FoedselData
	idx: number
}

type FoedselVisningTypes = {
	foedsel: FoedselData | FoedselsdatoData | FoedestedData
	idx: number
	data: Array<FoedselData> | Array<FoedselsdatoData> | Array<FoedestedData>
	tmpPersoner?: Array<FoedselData>
	ident?: string
	erPdlVisning?: boolean
	getInitialFoedsel: Function
	name: string
}

const FoedselLes = ({ foedsel, idx }: FoedselLesTypes) => {
	return (
		<div className="person-visning_redigerbar" key={idx}>
			<TitleValue title="Fødselsdato" value={formatDate(foedsel.foedselsdato)} />
			<TitleValue title="Fødselsår" value={foedsel.foedselsaar} />
			<TitleValue title="Fødested" value={foedsel.foedested} />
			<TitleValue title="Fødekommune">
				{foedsel.foedekommune && (
					<KodeverkConnector navn="KommunerMedHistoriske" value={foedsel.foedekommune}>
						{(_v: Kodeverk, verdi: KodeverkValues) => (
							<span>{verdi ? verdi.label : foedsel.foedekommune}</span>
						)}
					</KodeverkConnector>
				)}
			</TitleValue>
			<TitleValue
				title="Fødeland"
				value={foedsel.foedeland}
				kodeverk={AdresseKodeverk.StatsborgerskapLand}
			/>
			<TitleValue title="Master" value={foedsel.metadata?.master || foedsel.master} />
		</div>
	)
}

const FoedselVisning = ({
	foedsel,
	idx,
	tmpPersoner,
	data,
	erPdlVisning,
	ident,
	getInitialFoedsel,
	name,
}: FoedselVisningTypes) => {
	const initFoedsel = Object.assign(_.cloneDeep(getInitialFoedsel()), data[idx])
	const initialValues = { [name]: initFoedsel }

	const redigertFoedselPdlf = _.get(tmpPersoner, `${ident}.person.${name}`)?.find(
		(a: Person) => a.id === foedsel.id,
	)
	const slettetFoedselPdlf = tmpPersoner?.hasOwnProperty(ident) && !redigertFoedselPdlf
	if (slettetFoedselPdlf) {
		return <OpplysningSlettet />
	}

	const foedselValues = redigertFoedselPdlf ? redigertFoedselPdlf : foedsel
	const redigertFoedselValues = redigertFoedselPdlf
		? { [name]: Object.assign(_.cloneDeep(getInitialFoedsel()), redigertFoedselPdlf) }
		: null

	return erPdlVisning ? (
		<FoedselLes foedsel={foedsel} idx={idx} />
	) : (
		<VisningRedigerbar
			dataVisning={<FoedselLes foedsel={foedselValues} idx={idx} />}
			initialValues={initialValues}
			redigertAttributt={redigertFoedselValues}
			path={name}
			ident={ident}
		/>
	)
}

export const Foedsel = ({
	data,
	tmpPersoner,
	ident,
	erPdlVisning = false,
	erRedigerbar = true,
}: FoedselTypes) => {
	const { foedsel, foedested, foedselsdato } = data

	const harFoedsel = foedsel && foedsel.length > 0
	const harFoedested = foedested && foedested.length > 0
	const harFoedselsdato = foedselsdato && foedselsdato.length > 0

	if (!harFoedsel && !harFoedested && !harFoedselsdato) {
		return null
	}

	return (
		<div>
			<SubOverskrift label="Fødsel" iconKind="foedsel" />
			<div className="person-visning_content">
				<ErrorBoundary>
					{harFoedested || foedselsdato ? (
						<>
							{harFoedested && (
								<div className="person-visning_content" style={{ margin: '-15px 0 0 0' }}>
									<DollyFieldArray data={foedested} header="Fødested" nested>
										{(item: FoedestedData, idx: number) =>
											erRedigerbar ? (
												<FoedselVisning
													foedsel={item}
													idx={idx}
													data={foedested}
													ident={ident}
													erPdlVisning={erPdlVisning}
													tmpPersoner={tmpPersoner}
													getInitialFoedsel={getInitialFoedested}
													name="foedested"
												/>
											) : (
												<FoedselLes foedsel={item} idx={idx} />
											)
										}
									</DollyFieldArray>
								</div>
							)}
							{harFoedselsdato && (
								<div className="person-visning_content" style={{ margin: '-15px 0 0 0' }}>
									<DollyFieldArray data={foedselsdato} header="Fødselsdato" nested>
										{(item: FoedselsdatoData, idx: number) =>
											erRedigerbar ? (
												<FoedselVisning
													foedsel={item}
													idx={idx}
													data={foedselsdato}
													ident={ident}
													erPdlVisning={erPdlVisning}
													tmpPersoner={tmpPersoner}
													getInitialFoedsel={getInitialFoedselsdato}
													name="foedselsdato"
												/>
											) : (
												<FoedselLes foedsel={item} idx={idx} />
											)
										}
									</DollyFieldArray>
								</div>
							)}
						</>
					) : (
						<DollyFieldArray data={foedsel} header="" nested>
							{(item: FoedselData, idx: number) =>
								erRedigerbar ? (
									<FoedselVisning
										foedsel={item}
										idx={idx}
										//@ts-ignore
										data={foedsel}
										ident={ident}
										erPdlVisning={erPdlVisning}
										tmpPersoner={tmpPersoner}
										getInitialFoedsel={getInitialFoedsel}
										name="foedsel"
									/>
								) : (
									<FoedselLes foedsel={item} idx={idx} />
								)
							}
						</DollyFieldArray>
					)}
				</ErrorBoundary>
			</div>
		</div>
	)
}
