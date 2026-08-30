import { Country, State } from 'country-state-city';

export interface CountryOption {
  name: string;
  isoCode: string;
  currency: string;
}

export interface StateOption {
  name: string;
  isoCode: string;
}

export function getAllCountries(): CountryOption[] {
  return Country.getAllCountries()
    .map((country) => ({ name: country.name, isoCode: country.isoCode, currency: country.currency }))
    .sort((a, b) => a.name.localeCompare(b.name));
}

export function getStatesOfCountry(countryIsoCode: string): StateOption[] {
  return State.getStatesOfCountry(countryIsoCode)
    .map((state) => ({ name: state.name, isoCode: state.isoCode }))
    .sort((a, b) => a.name.localeCompare(b.name));
}
