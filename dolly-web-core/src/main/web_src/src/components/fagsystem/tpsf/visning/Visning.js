import React from 'react'
import _has from 'lodash/has'
import _omit from 'lodash/omit'
import { Personinfo } from './partials/Personinfo'
import { Nasjonalitet } from './partials/Nasjonalitet'
import { Boadresse } from './partials/Boadresse'
import { Postadresse } from './partials/Postadresse'
import { Identhistorikk } from './partials/Identhistorikk'
import { Relasjoner } from './partials/Relasjoner'

export const TpsfVisning = ({ data }) => {
	if (!data) return null

	return (
		<div>
			<Personinfo data={data} />
			<Nasjonalitet data={data} />
			<Boadresse boadresse={data.boadresse} />
			<Postadresse postadresse={data.postadresse} />
			<Identhistorikk identhistorikk={data.identHistorikk} />
			<Relasjoner relasjoner={data.relasjoner} />
		</div>
	)
}

/**
 * Denne funksjonen brukes til å fjerne verdier vi ikke ønsker vise, men som automatisk er med
 * på objektet vi får fra API. Kan feks være verdier som ikke er bestilt, men som får default
 * verdier som er uinteressante for bruker
 */
TpsfVisning.filterValues = (data, tpsfKriterier) => {
	// Fast bopel
	if (!tpsfKriterier.utenFastBopel) data = _omit(data, 'utenFastBopel')

	// Innvandret
	if (!tpsfKriterier.innvandretFraLand) {
		data = _omit(data, ['innvandretFraLand', 'innvandretFraLandFlyttedato'])
	}

	// Relasjoner
	if (!_has(tpsfKriterier, 'relasjoner.partner.innvandretFraLand')) {
		data = _omit(data, [
			'relasjoner.partner.innvandretFraLand',
			'relasjoner.partner.innvandretFraLandFlyttedato'
		])
	}

	if (_has(tpsfKriterier, 'relasjoner.barn') && tpsfKriterier.relasjoner.barn !== null) {
		tpsfKriterier.relasjoner.barn.forEach((barn, idx) => {
			if (!barn.innvandretFraLand) {
				data = _omit(data, [
					`relasjoner.barn[${idx}].innvandretFraLand`,
					`relasjoner.barn[${idx}].innvandretFraLandFlyttedato`
				])
			}
		})
	}

	return data
}
