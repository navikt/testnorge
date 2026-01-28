import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle } from '@/components/bestillingsveileder/stegVelger/steg/steg3/Bestillingsvisning'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import {
	codeToNorskLabel,
	formatDate,
	formatDateTime,
	oversettBoolean,
} from '@/utils/DataFormatter'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import React from 'react'
import { stadieKodeverk } from '@/components/fagsystem/sigrunstubSummertSkattegrunnlag/form/Form'
import {
	kategoriKodeverk,
	tekniskNavnKodeverk,
} from '@/components/fagsystem/sigrunstubSummertSkattegrunnlag/form/GrunnlagArrayForm'
import { EkspanderbarVisning } from '@/components/bestilling/sammendrag/partials/EkspanderbarVisning'

const SpesifiseringVisning = ({ spesifisering, key }) => {
	return (
		<React.Fragment key={key}>
			<TitleValue title="Type" value={codeToNorskLabel(spesifisering.type)} />
			<TitleValue
				title="År for førstegangsreg."
				value={spesifisering.aarForFoerstegangsregistrering}
			/>
			<TitleValue title="Antatt markedsverdi" value={spesifisering.antattMarkedsverdi} />
			<TitleValue title="Antatt verdi som nytt" value={spesifisering.antattVerdiSomNytt} />
			<TitleValue title="Beløp" value={spesifisering.beloep} />
			<TitleValue title="Eierandel" value={spesifisering.eierandel} />
			<TitleValue title="Fabrikatnavn" value={spesifisering.fabrikatnavn} />
			<TitleValue title="Formuesverdi" value={spesifisering.formuesverdi} />
			<TitleValue
				title="Formuesverdi for formuesandel"
				value={spesifisering.formuesverdiForFormuesandel}
			/>
			<TitleValue title="Registreringsnummer" value={spesifisering.registreringsnummer} />
		</React.Fragment>
	)
}

const GrunnlagVisning = ({ grunnlag, key }) => {
	return (
		<React.Fragment key={key}>
			<TitleValue
				title="Teknisk navn"
				value={grunnlag.tekniskNavn}
				kodeverk={tekniskNavnKodeverk}
			/>
			<TitleValue title="Beløp" value={grunnlag.beloep} />
			<TitleValue title="Kategori" value={grunnlag.kategori} kodeverk={kategoriKodeverk} />
			<TitleValue title="Andel fra barn" value={grunnlag.andelOverfoertFraBarn} />
			<EkspanderbarVisning vis={grunnlag.spesifisering?.length > 0} header="SPESIFISERING">
				<DollyFieldArray
					header="Spesifisering"
					data={grunnlag.spesifisering}
					nested
					whiteBackground
				>
					{(spesifisering, idy) => (
						<SpesifiseringVisning spesifisering={spesifisering} key={'spesifisering' + key + idy} />
					)}
				</DollyFieldArray>
			</EkspanderbarVisning>
		</React.Fragment>
	)
}

export const SigrunstubSummertSkattegrunnlag = ({ summertSkattegrunnlagListe }) => {
	if (!summertSkattegrunnlagListe || summertSkattegrunnlagListe.length === 0) {
		return null
	}

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Summert skattegrunnlag (Sigrun)</BestillingTitle>
				<DollyFieldArray header="Summert skattegrunnlag" data={summertSkattegrunnlagListe}>
					{(skattegrunnlag, idx) => {
						return (
							<React.Fragment key={idx}>
								<TitleValue title="Inntektsår" value={skattegrunnlag?.inntektsaar} />
								<TitleValue
									title="Ajourholdstidspunkt"
									value={formatDateTime(skattegrunnlag?.ajourholdstidspunkt)}
								/>
								<TitleValue
									title="Skatteoppgjørsdato"
									value={formatDate(skattegrunnlag?.skatteoppgjoersdato)}
								/>
								<TitleValue title="Skjermet" value={oversettBoolean(skattegrunnlag?.skjermet)} />
								<TitleValue
									title="Stadie"
									value={skattegrunnlag?.stadie}
									kodeverk={stadieKodeverk}
								/>
								{skattegrunnlag?.grunnlag?.length > 0 && (
									<DollyFieldArray header="Grunnlag" data={skattegrunnlag?.grunnlag} nested>
										{(grunnlag, idy) => (
											<GrunnlagVisning grunnlag={grunnlag} key={'grunnlag' + idy} />
										)}
									</DollyFieldArray>
								)}
								{skattegrunnlag?.kildeskattPaaLoennGrunnlag?.length > 0 && (
									<DollyFieldArray
										header="Kildeskatt på lønnsgrunnlag"
										data={skattegrunnlag?.kildeskattPaaLoennGrunnlag}
										nested
									>
										{(grunnlag, idy) => (
											<GrunnlagVisning grunnlag={grunnlag} key={'kildeskatt' + idy} />
										)}
									</DollyFieldArray>
								)}
								{skattegrunnlag?.svalbardGrunnlag?.length > 0 && (
									<DollyFieldArray
										header="Svalbard grunnlag"
										data={skattegrunnlag?.svalbardGrunnlag}
										nested
									>
										{(grunnlag, idy) => (
											<GrunnlagVisning grunnlag={grunnlag} key={'svalbard' + idy} />
										)}
									</DollyFieldArray>
								)}
							</React.Fragment>
						)
					}}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
