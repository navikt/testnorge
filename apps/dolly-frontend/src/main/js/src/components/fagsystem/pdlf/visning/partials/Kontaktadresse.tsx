import React from 'react'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { Vegadresse } from '@/components/fagsystem/pdlf/visning/partials/Vegadresse'
import { UtenlandskAdresse } from '@/components/fagsystem/pdlf/visning/partials/UtenlandskAdresse'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import KodeverkConnector from '@/components/kodeverk/KodeverkConnector'
import {
	Kodeverk,
	KodeverkValues,
	KontaktadresseData,
} from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import * as _ from 'lodash-es'
import { AdresseKodeverk } from '@/config/kodeverk'
import { getInitialKontaktadresse } from '@/components/fagsystem/pdlf/form/initialValues'
import VisningRedigerbarConnector from '@/components/fagsystem/pdlf/visning/visningRedigerbar/VisningRedigerbarConnector'
import { formatDate } from '@/utils/DataFormatter'
import { OpplysningSlettet } from '@/components/fagsystem/pdlf/visning/visningRedigerbar/OpplysningSlettet'

type KontaktadresseTypes = {
	data: Array<any>
	tmpPersoner?: Array<KontaktadresseData>
	ident?: number
	erPdlVisning?: boolean
	identtype?: string
	erRedigerbar?: boolean
}

type KontaktadresseVisningTypes = {
	kontaktadresseData: any
	idx: number
	data: any
	tmpData: any
	tmpPersoner: Array<KontaktadresseData>
	ident: number
	erPdlVisning: boolean
	identtype?: string
	master?: string
}

type AdresseTypes = {
	kontaktadresseData: any
	idx: number
}

const Adressedatoer = ({ kontaktadresseData }: any) => (
	<>
		<TitleValue title="Gyldig fra og med" value={formatDate(kontaktadresseData.gyldigFraOgMed)} />
		<TitleValue title="Gyldig til og med" value={formatDate(kontaktadresseData.gyldigTilOgMed)} />
	</>
)

export const Adresse = ({ kontaktadresseData, idx }: AdresseTypes) => {
	if (!kontaktadresseData) {
		return null
	}

	return (
		<>
			{kontaktadresseData.vegadresse && <Vegadresse adresse={kontaktadresseData} idx={idx} />}
			{kontaktadresseData.utenlandskAdresse && (
				<UtenlandskAdresse adresse={kontaktadresseData} idx={idx} />
			)}
			{kontaktadresseData.postboksadresse && (
				<>
					<h4 style={{ marginTop: '0px' }}>Postboksadresse</h4>
					<div className="person-visning_content" key={idx}>
						<TitleValue
							title="Postbokseier"
							value={kontaktadresseData.postboksadresse.postbokseier}
						/>
						<TitleValue title="Postboks" value={kontaktadresseData.postboksadresse.postboks} />
						<TitleValue title="Postnummer">
							{kontaktadresseData.postboksadresse.postnummer && (
								<KodeverkConnector
									navn="Postnummer"
									value={kontaktadresseData.postboksadresse.postnummer}
								>
									{(_v: Kodeverk, verdi: KodeverkValues) => (
										<span>
											{verdi ? verdi.label : kontaktadresseData.postboksadresse.postnummer}
										</span>
									)}
								</KodeverkConnector>
							)}
						</TitleValue>
						<Adressedatoer kontaktadresseData={kontaktadresseData} />
						<TitleValue title="C/O adressenavn" value={kontaktadresseData.coAdressenavn} />
						<TitleValue
							title="Master"
							value={kontaktadresseData.master || kontaktadresseData.metadata?.master}
						/>
					</div>
				</>
			)}
			{kontaktadresseData.postadresseIFrittFormat && (
				<>
					<h4 style={{ marginTop: '0px' }}>Postadresse i fritt format</h4>
					<div className="person-visning_content" key={idx}>
						{kontaktadresseData.postadresseIFrittFormat.adresselinjer && (
							<TitleValue title="Adresselinjer">
								{kontaktadresseData.postadresseIFrittFormat.adresselinjer
									?.filter((l: string) => l)
									.map((l: string, i: number) => (
										<div key={i}>{l}</div>
									))}
							</TitleValue>
						)}
						<TitleValue title="Postnummer">
							{kontaktadresseData.postadresseIFrittFormat.postnummer && (
								<KodeverkConnector
									navn="Postnummer"
									value={kontaktadresseData.postadresseIFrittFormat.postnummer}
								>
									{(_v: Kodeverk, verdi: KodeverkValues) => (
										<span>
											{verdi ? verdi.label : kontaktadresseData.postadresseIFrittFormat.postnummer}
										</span>
									)}
								</KodeverkConnector>
							)}
						</TitleValue>
						<Adressedatoer kontaktadresseData={kontaktadresseData} />
						<TitleValue
							title="Master"
							value={kontaktadresseData.master || kontaktadresseData.metadata?.master}
						/>
					</div>
				</>
			)}
			{kontaktadresseData.utenlandskAdresseIFrittFormat && (
				<>
					<h4 style={{ marginTop: '0px' }}>Utenlandsk adresse i fritt format</h4>
					<div className="person-visning_content" key={idx}>
						{kontaktadresseData.utenlandskAdresseIFrittFormat.adresselinjer && (
							<TitleValue title="Adresselinjer">
								{kontaktadresseData.utenlandskAdresseIFrittFormat.adresselinjer
									?.filter((l: string) => l)
									.map((l: string, i: number) => (
										<div key={i}>{l}</div>
									))}
							</TitleValue>
						)}
						<TitleValue
							title="Postkode"
							value={kontaktadresseData.utenlandskAdresseIFrittFormat.postkode}
						/>
						<TitleValue
							title="By eller sted"
							value={kontaktadresseData.utenlandskAdresseIFrittFormat.byEllerStedsnavn}
						/>
						<TitleValue
							title="Land"
							value={kontaktadresseData.utenlandskAdresseIFrittFormat.landkode}
							kodeverk={AdresseKodeverk.StatsborgerskapLand}
						/>
						<Adressedatoer kontaktadresseData={kontaktadresseData} />
						<TitleValue
							title="Master"
							value={kontaktadresseData.master || kontaktadresseData.metadata?.master}
						/>
					</div>
				</>
			)}
		</>
	)
}

export const KontaktadresseVisning = ({
	kontaktadresseData,
	idx,
	data,
	tmpData,
	tmpPersoner,
	ident,
	erPdlVisning,
	identtype,
	master,
}: KontaktadresseVisningTypes) => {
	const initKontaktadresse = Object.assign(
		_.cloneDeep(getInitialKontaktadresse()),
		data?.[idx] || tmpData?.[idx],
	)
	const initialValues = { kontaktadresse: initKontaktadresse }

	const redigertKontaktadressePdlf = _.get(tmpPersoner, `${ident}.person.kontaktadresse`)?.find(
		(a: KontaktadresseData) => a.id === kontaktadresseData.id,
	)
	const slettetKontaktadressePdlf =
		tmpPersoner?.hasOwnProperty(ident) && !redigertKontaktadressePdlf
	if (slettetKontaktadressePdlf) {
		return <OpplysningSlettet />
	}

	const kontaktadresseValues = redigertKontaktadressePdlf
		? redigertKontaktadressePdlf
		: kontaktadresseData
	const redigertKontaktadresseValues = redigertKontaktadressePdlf
		? {
				kontaktadresse: Object.assign(
					_.cloneDeep(getInitialKontaktadresse()),
					redigertKontaktadressePdlf,
				),
			}
		: null
	return erPdlVisning ? (
		<Adresse kontaktadresseData={kontaktadresseData} idx={idx} />
	) : (
		<VisningRedigerbarConnector
			dataVisning={<Adresse kontaktadresseData={kontaktadresseValues} idx={idx} />}
			initialValues={initialValues}
			redigertAttributt={redigertKontaktadresseValues}
			path="kontaktadresse"
			ident={ident}
			identtype={identtype}
			master={master}
		/>
	)
}

export const Kontaktadresse = ({
	data,
	tmpPersoner,
	ident,
	erPdlVisning = false,
	identtype,
	erRedigerbar = true,
}: KontaktadresseTypes) => {
	if ((!data || data.length === 0) && (!tmpPersoner || Object.keys(tmpPersoner).length < 1)) {
		return null
	}

	const tmpData = _.get(tmpPersoner, `${ident}.person.kontaktadresse`)
	if ((!data || data.length === 0) && (!tmpData || tmpData.length < 1)) {
		return null
	}

	return (
		<>
			<SubOverskrift label="Kontaktadresse" iconKind="postadresse" />
			<div className="person-visning_content">
				<ErrorBoundary>
					<DollyFieldArray data={data || tmpData} header="" nested>
						{(adresse: any, idx: number) =>
							erRedigerbar ? (
								<KontaktadresseVisning
									kontaktadresseData={adresse}
									idx={idx}
									data={data}
									tmpData={tmpData}
									ident={ident}
									erPdlVisning={erPdlVisning}
									tmpPersoner={tmpPersoner}
									identtype={identtype}
								/>
							) : (
								<Adresse kontaktadresseData={adresse} idx={idx} />
							)
						}
					</DollyFieldArray>
				</ErrorBoundary>
			</div>
		</>
	)
}
