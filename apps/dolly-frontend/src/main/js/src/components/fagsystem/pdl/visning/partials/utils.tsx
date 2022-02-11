import React from 'react'
import { Sivilstand } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'

export const getSortedSivilstand = (sivilstander: Sivilstand[]) => {
	if (!sivilstander || sivilstander.length === 0) return sivilstander

	return [...sivilstander].sort(function (a: Sivilstand, b: Sivilstand) {
		if (a.gyldigFraOgMed < b.gyldigFraOgMed) return 1
		if (a.gyldigFraOgMed > b.gyldigFraOgMed) return -1
		return 0
	})
}
