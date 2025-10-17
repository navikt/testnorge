import { addDays, isBefore } from 'date-fns'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatAlder, formatDate, formatKjonn, gtTypeLabel } from '@/utils/DataFormatter'
import { GtKodeverk, PersoninformasjonKodeverk } from '@/config/kodeverk'
import * as _ from 'lodash-es'
import { getAlder } from '@/ducks/fagsystem'

function hentSkjermingData(skjermingPath) {
	if (!skjermingPath) {
		return null
	}
	return (
		<>
			{skjermingPath.egenAnsattDatoFom && (
				<>
					<TitleValue
						title="Har skjerming (egenansatt)"
						value={
							skjermingPath.egenAnsattDatoTom &&
							isBefore(new Date(skjermingPath.egenAnsattDatoTom), addDays(new Date(), -1))
								? 'NEI'
								: 'JA'
						}
					/>
					<TitleValue title="Skjerming fra" value={formatDate(skjermingPath.egenAnsattDatoFom)} />
					{skjermingPath.egenAnsattDatoTom && (
						<TitleValue title="Skjerming til" value={formatDate(skjermingPath.egenAnsattDatoTom)} />
					)}
				</>
			)}
		</>
	)
}

function hentSikkerhetstiltakData(sikkerhetstiltakPath) {
	if (!sikkerhetstiltakPath) {
		return null
	}
	return (
		<div className="person-visning_content">
			<h4 style={{ marginTop: '5px' }}>Sikkerhetstiltak</h4>
			{sikkerhetstiltakPath.typeSikkerhetTiltak && (
				<div className="person-visning_content">
					<TitleValue
						title="Type sikkerhetstiltak"
						value={`${sikkerhetstiltakPath.typeSikkerhetTiltak} - ${sikkerhetstiltakPath.beskrSikkerhetTiltak}`}
					/>
					<TitleValue
						title="Sikkerhetstiltak starter"
						value={formatDate(sikkerhetstiltakPath.sikkerhetTiltakDatoFom)}
					/>
					<TitleValue
						title="Sikkerhetstiltak opphører"
						value={formatDate(sikkerhetstiltakPath.sikkerhetTiltakDatoTom)}
					/>
				</div>
			)}
			{sikkerhetstiltakPath.tiltakstype && (
				<div className="person-visning_content">
					<TitleValue
						title="Type sikkerhetstiltak"
						value={`${sikkerhetstiltakPath.tiltakstype} - ${sikkerhetstiltakPath.beskrivelse}`}
					/>
					<TitleValue
						title="Sikkerhetstiltak starter"
						value={formatDate(sikkerhetstiltakPath.gyldigFraOgMed)}
					/>
					<TitleValue
						title="Sikkerhetstiltak opphører"
						value={formatDate(sikkerhetstiltakPath.gyldigTilOgMed)}
					/>
				</div>
			)}
		</div>
	)
}

export const TpsfPersoninfo = ({ data, fagsystemData = {}, visTittel = true, pdlData }) => {
	const harPdlAdressebeskyttelse = pdlData && _.has(pdlData, 'adressebeskyttelse')
	const harPdlUfb = pdlData && _.has(pdlData, 'bostedsadresse[0].ukjentBosted')

	const tpsMessaging = fagsystemData?.tpsMessaging
	const alder = getAlder(data.alder, data.doedsdato, data.ident)

	return (
		<div>
			{visTittel && <SubOverskrift label="Persondetaljer" iconKind="personinformasjon" />}
			<div className="person-visning_content">
				<TitleValue title={data.identtype} value={data.ident} visKopier />
				<TitleValue title="Fornavn" value={data.fornavn} />

				<TitleValue title="Mellomnavn" value={data.mellomnavn} />

				<TitleValue title="Etternavn" value={data.etternavn} />

				<TitleValue title="Kjønn" value={formatKjonn(data.kjonn, data.alder)} />
				<TitleValue title="Alder" value={formatAlder(alder, data.doedsdato)} />
				<TitleValue title="Dødsdato" value={formatDate(data.doedsdato)} />

				<TitleValue
					title="Personstatus"
					kodeverk={PersoninformasjonKodeverk.Personstatuser}
					value={data.personStatus}
				/>

				<TitleValue title="Savnet siden" value={formatDate(data.forsvunnetDato)} />

				<TitleValue
					title="Sivilstand"
					kodeverk={PersoninformasjonKodeverk.Sivilstander}
					value={data.sivilstand instanceof String ? data.sivilstand : data.sivilstand?.sivilstand}
				/>

				<TitleValue title="Bankkontonummer" value={data.bankkontonr} />

				<TitleValue title="Bankkonto opprettet" value={formatDate(data.bankkontonrRegdato)} />

				<TitleValue
					title={data.telefonnummer_2 ? 'Telefonnummer 1' : 'Telefonnummer'}
					value={data.telefonnummer_1 && `${data.telefonLandskode_1} ${data.telefonnummer_1}`}
				/>

				<TitleValue
					title="Telefonnummer 2"
					value={data.telefonnummer_2 && `${data.telefonLandskode_2} ${data.telefonnummer_2}`}
				/>

				{data.spesreg !== 'UFB' && !harPdlAdressebeskyttelse && (
					<TitleValue
						title="Diskresjonskoder"
						kodeverk={PersoninformasjonKodeverk.Diskresjonskoder}
						value={data.spesreg}
					/>
				)}

				{(data.utenFastBopel || data.spesreg === 'UFB') && !harPdlUfb && (
					<TitleValue title="Uten fast bopel" value="JA" />
				)}

				{data.gtVerdi && (
					<TitleValue
						title="Geo. tilhør."
						kodeverk={GtKodeverk[data.gtType]}
						value={data.gtVerdi}
						size="medium"
					>
						{(value) => `${gtTypeLabel(data.gtType)} - ${value.label}`}
					</TitleValue>
				)}

				<TitleValue
					title="TK-nummer"
					value={data.tknavn ? `${data.tknr} - ${data.tknavn}` : data.tknr}
					size="medium"
				/>
				{hentSkjermingData(tpsMessaging?.egenAnsattDatoFom ? tpsMessaging : data)}
				{hentSikkerhetstiltakData(tpsMessaging?.sikkerhetstiltak || data?.sikkerhetstiltak)}
			</div>
		</div>
	)
}
