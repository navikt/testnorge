import * as React from 'react'
import _get from 'lodash/get'
import _has from 'lodash/has'
import Formatters from '~/utils/DataFormatter'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { Bidiagnoser } from './Bidiagnoser'
import { Perioder } from './Perioder'
import { ArbeidKodeverk } from '~/config/kodeverk'
import { SykemeldingDetaljert } from '~/components/fagsystem/sykdom/SykemeldingTypes'

export const DetaljertSykemelding = ({ sykemelding, idx }: SykemeldingDetaljert) => (
	<div key={idx} className="person-visning_content">
		<React.Fragment key={idx}>
			<div className="person-visning_content">
				<TitleValue title="Startdato" value={Formatters.formatStringDates(sykemelding.startDato)} />
				<TitleValue
					title="Trenger umiddelbar bistand"
					value={Formatters.oversettBoolean(sykemelding.umiddelbarBistand)}
				/>
				<TitleValue
					title=" Manglende tilrettelegging på arbeidsplassen"
					value={Formatters.oversettBoolean(sykemelding.manglendeTilretteleggingPaaArbeidsplassen)}
				/>
			</div>
			<>
				<h4>Diagnose</h4>
				<div className="person-visning_content">
					<TitleValue title="Diagnose" value={sykemelding.hovedDiagnose.diagnose} />
					<TitleValue title="Diagnosekode" value={sykemelding.hovedDiagnose.diagnosekode} />
				</div>
			</>
			<Bidiagnoser data={sykemelding.biDiagnoser} />
			<>
				<h4>Helsepersonell</h4>
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
				<h4>Arbeidsgiver</h4>
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
				<h4>Detaljer</h4>
				<div className="person-visning_content">
					<TitleValue title="Tiltak fra nav" value={sykemelding.detaljer.tiltakNav} />
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
						value={Formatters.oversettBoolean(sykemelding.detaljer.arbeidsforEtterEndtPeriode)}
					/>
				</div>
			</>
		</React.Fragment>
	</div>
)
