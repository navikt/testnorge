import * as React from 'react'
import { formatStringDates, oversettBoolean } from '@/utils/DataFormatter'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { Bidiagnoser } from './Bidiagnoser'
import { Perioder } from './Perioder'
import { ArbeidKodeverk } from '@/config/kodeverk'
import { SykemeldingDetaljert } from '@/components/fagsystem/sykdom/SykemeldingTypes'
import styled from 'styled-components'

export const SykemeldingKategori = styled.div`
	width: 100%;

	h4 {
		margin-top: 5px;
		margin-bottom: 15px;
	}
`

export const DetaljertSykemelding = ({ sykemelding, idx }: SykemeldingDetaljert) => (
	<div key={idx} className="person-visning_content">
		<React.Fragment key={idx}>
			<div className="person-visning_content">
				<TitleValue title="Startdato" value={formatStringDates(sykemelding.startDato)} />
				<TitleValue
					title="Trenger umiddelbar bistand"
					value={oversettBoolean(sykemelding.umiddelbarBistand)}
				/>
				<TitleValue
					title=" Manglende tilrettelegging på arbeidsplassen"
					value={oversettBoolean(sykemelding.manglendeTilretteleggingPaaArbeidsplassen)}
				/>
			</div>
			<>
				<SykemeldingKategori>
					<h4>Diagnose</h4>
				</SykemeldingKategori>
				<div className="person-visning_content">
					<TitleValue title="Diagnose" value={sykemelding.hovedDiagnose.diagnose} />
					<TitleValue title="Diagnosekode" value={sykemelding.hovedDiagnose.diagnosekode} />
				</div>
			</>
			<Bidiagnoser data={sykemelding.biDiagnoser} />
			{sykemelding.kontaktMedPasient && (
				<>
					<SykemeldingKategori>
						<h4>Tilbakedatering</h4>
					</SykemeldingKategori>
					<div className="person-visning_content">
						<TitleValue
							title="Begrunnelse ikke kontakt"
							value={sykemelding.kontaktMedPasient.begrunnelseIkkeKontakt}
						/>
						<TitleValue title="Kontaktdato" value={sykemelding.kontaktMedPasient.kontaktDato} />
					</div>
				</>
			)}
			<>
				<SykemeldingKategori>
					<h4>Helsepersonell</h4>
				</SykemeldingKategori>
				<div className="person-visning_content">
					<TitleValue
						title="Navn"
						value={`${sykemelding.helsepersonell.fornavn} ${
							sykemelding.helsepersonell.mellomnavn ? sykemelding.helsepersonell.mellomnavn : ''
						} ${sykemelding.helsepersonell.etternavn}`}
					/>
					<TitleValue title="Ident" value={sykemelding.helsepersonell.ident} />
					<TitleValue title="HPR-nummer" value={sykemelding.helsepersonell.hprId} />
					<TitleValue title="SamhandlerType" value={sykemelding.helsepersonell.samhandlerType} />
				</div>
			</>
			<>
				<SykemeldingKategori>
					<h4>Arbeidsgiver</h4>
				</SykemeldingKategori>
				<div className="person-visning_content">
					<TitleValue title="Navn" value={sykemelding.arbeidsgiver.navn} />
					<TitleValue
						title="Yrkesbetegnelse"
						value={sykemelding.arbeidsgiver.yrkesbetegnelse}
						kodeverk={ArbeidKodeverk.Yrker}
					/>
					<TitleValue title="Stillingsprosent" value={sykemelding.arbeidsgiver.stillingsprosent} />
				</div>
			</>
			<Perioder data={sykemelding.perioder} />
			<>
				<SykemeldingKategori>
					<h4>Detaljer</h4>
				</SykemeldingKategori>
				<div className="person-visning_content">
					<TitleValue title="Tiltak fra Nav" value={sykemelding.detaljer.tiltakNav} />
					<TitleValue
						title="Tiltak på arbeidsplass"
						value={sykemelding.detaljer.tiltakArbeidsplass}
					/>
					<TitleValue
						title="Hensyn på arbeidsplass"
						value={sykemelding.detaljer.beskrivHensynArbeidsplassen}
					/>
					<TitleValue
						title="Arbeidsfør etter endt periode"
						value={oversettBoolean(sykemelding.detaljer.arbeidsforEtterEndtPeriode)}
					/>
					<TitleValue
						title="Referanse for sporing av innsending"
						value={sykemelding.sykemeldingId}
					/>
				</div>
			</>
		</React.Fragment>
	</div>
)
