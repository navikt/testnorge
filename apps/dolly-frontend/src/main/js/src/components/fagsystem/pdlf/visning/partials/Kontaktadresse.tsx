import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { Vegadresse } from '~/components/fagsystem/pdlf/visning/partials/Vegadresse'
import { UtenlandskAdresse } from '~/components/fagsystem/pdlf/visning/partials/UtenlandskAdresse'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import KodeverkConnector from '~/components/kodeverk/KodeverkConnector'
import {
	Kodeverk,
	KodeverkValues,
} from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { AdresseKodeverk } from '~/config/kodeverk'

type Data = {
	data: Array<any>
}

type AdresseProps = {
	adresse: any
	idx: number
}

export const Adresse = ({ adresse, idx }: AdresseProps) => {
	return (
		<>
			{adresse.vegadresse && <Vegadresse adresse={adresse} idx={idx} />}
			{adresse.utenlandskAdresse && <UtenlandskAdresse adresse={adresse} idx={idx} />}
			{adresse.postboksadresse && (
				<>
					<h4 style={{ marginTop: '0px' }}>Postboksadresse</h4>
					<div className="person-visning_content" key={idx}>
						<TitleValue title="Postbokseier" value={adresse.postboksadresse.postbokseier} />
						<TitleValue title="Postboks" value={adresse.postboksadresse.postboks} />
						<TitleValue title="Postnummer">
							{adresse.postboksadresse.postnummer && (
								<KodeverkConnector navn="Postnummer" value={adresse.postboksadresse.postnummer}>
									{(v: Kodeverk, verdi: KodeverkValues) => (
										<span>{verdi ? verdi.label : adresse.postboksadresse.postnummer}</span>
									)}
								</KodeverkConnector>
							)}
						</TitleValue>
					</div>
				</>
			)}
			{adresse.postadresseIFrittFormat && (
				<>
					<h4 style={{ marginTop: '0px' }}>Postadresse i fritt format</h4>
					<div className="person-visning_content" key={idx}>
						{adresse.postadresseIFrittFormat.adresselinjer ? (
							<TitleValue title="Adresselinjer">
								<div>{adresse.postadresseIFrittFormat.adresselinjer[0]}</div>
								<div>{adresse.postadresseIFrittFormat.adresselinjer[1]}</div>
								<div>{adresse.postadresseIFrittFormat.adresselinjer[2]}</div>
							</TitleValue>
						) : (
							<TitleValue title="Adresselinjer">
								<div>{adresse.postadresseIFrittFormat.adresselinje1}</div>
								<div>{adresse.postadresseIFrittFormat.adresselinje2}</div>
								<div>{adresse.postadresseIFrittFormat.adresselinje3}</div>
							</TitleValue>
						)}
						<TitleValue title="Postnummer">
							{adresse.postadresseIFrittFormat.postnummer && (
								<KodeverkConnector
									navn="Postnummer"
									value={adresse.postadresseIFrittFormat.postnummer}
								>
									{(v: Kodeverk, verdi: KodeverkValues) => (
										<span>{verdi ? verdi.label : adresse.postadresseIFrittFormat.postnummer}</span>
									)}
								</KodeverkConnector>
							)}
						</TitleValue>
					</div>
				</>
			)}
			{adresse.utenlandskAdresseIFrittFormat && (
				<>
					<h4 style={{ marginTop: '0px' }}>Utenlandsk adresse i fritt format</h4>
					<div className="person-visning_content" key={idx}>
						{adresse.utenlandskAdresseIFrittFormat.adresselinjer ? (
							<TitleValue title="Adresselinjer">
								<div>{adresse.utenlandskAdresseIFrittFormat.adresselinjer[0]}</div>
								<div>{adresse.utenlandskAdresseIFrittFormat.adresselinjer[1]}</div>
								<div>{adresse.utenlandskAdresseIFrittFormat.adresselinjer[2]}</div>
							</TitleValue>
						) : (
							<TitleValue title="Adresselinjer">
								<div>{adresse.utenlandskAdresseIFrittFormat.adresselinje1}</div>
								<div>{adresse.utenlandskAdresseIFrittFormat.adresselinje2}</div>
								<div>{adresse.utenlandskAdresseIFrittFormat.adresselinje3}</div>
							</TitleValue>
						)}
						<TitleValue title="Postkode" value={adresse.utenlandskAdresseIFrittFormat.postkode} />
						<TitleValue
							title="By eller sted"
							value={adresse.utenlandskAdresseIFrittFormat.byEllerStedsnavn}
						/>
						<TitleValue
							title="Land"
							value={adresse.utenlandskAdresseIFrittFormat.landkode}
							kodeverk={AdresseKodeverk.StatsborgerskapLand}
						/>
					</div>
				</>
			)}
		</>
	)
}

export const Kontaktadresse = ({ data }: Data) => {
	if (!data || data.length === 0) return null

	return (
		<>
			<SubOverskrift label="Kontaktadresse" iconKind="postadresse" />
			<div className="person-visning_content">
				<ErrorBoundary>
					<DollyFieldArray data={data} header="" nested>
						{(adresse: any, idx: number) => <Adresse adresse={adresse} idx={idx} />}
					</DollyFieldArray>
				</ErrorBoundary>
			</div>
		</>
	)
}
