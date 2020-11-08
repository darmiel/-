"use strict";

/**
 *
 * @param {Response} res
 * @param {import("joi").AnySchema} schema
 * @param {any} v
 */
module.exports.validate = (res, schema, v) => {
  const { error, value } = schema.validate(v);
  if (error) {
      res.status(200).json({
          error: true,
          message: error.details[0].message
      });
      return undefined;
  }
  return value;
};